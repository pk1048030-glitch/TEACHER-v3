package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.network.GeminiApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object TeacherHome : Screen()
    data class TeacherCourseDetail(val courseId: Int) : Screen()
    data class TeacherLessonDetail(val lessonId: Int) : Screen()
    object StudentHome : Screen()
    data class StudentTopicDetail(val topicId: Int) : Screen()
    object StudentQuiz : Screen()
    object AiAssistant : Screen()
    object StoryAndActivity : Screen()
    data class StoryDetail(val storyId: Int) : Screen()
    object DrawingCanvas : Screen()
    object Analytics : Screen()
}

class AcademyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = AcademyRepository(database.academyDao())

    // --- NAVIGATION STATE ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.TeacherHome)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // --- SCREEN NAVIGATION STACK (SIMPLE BACK BUTTON HANDLING) ---
    private val backStack = mutableListOf<Screen>()

    fun navigateToWithBack(screen: Screen) {
        backStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    // --- BOOKMARKS ---
    val bookmarksState: StateFlow<List<SavedBookmark>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- TEACHER HUB DATA ---
    val courses: StateFlow<List<TeacherCourse>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCourseId = MutableStateFlow<Int?>(null)
    val selectedCourseId: StateFlow<Int?> = _selectedCourseId.asStateFlow()

    val courseLessons: StateFlow<List<TeacherLesson>> = combine(
        _selectedCourseId,
        repository.allCourses // triggered on updates
    ) { id, _ ->
        id?.let {
            var lessonsList = emptyList<TeacherLesson>()
            repository.getLessonsByCourse(it).collect { list -> lessonsList = list }
            lessonsList
        } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCourse(id: Int) {
        _selectedCourseId.value = id
        navigateToWithBack(Screen.TeacherCourseDetail(id))
    }

    fun selectLesson(id: Int) {
        navigateToWithBack(Screen.TeacherLessonDetail(id))
    }

    fun toggleLessonCompleted(lessonId: Int) {
        viewModelScope.launch {
            repository.getLessonById(lessonId)?.let {
                val updated = it.copy(isCompleted = !it.isCompleted)
                repository.updateLesson(updated)
                updateGlobalProgress()
            }
        }
    }

    fun toggleLessonBookmarked(lessonId: Int) {
        viewModelScope.launch {
            repository.getLessonById(lessonId)?.let {
                val updated = it.copy(isBookmarked = !it.isBookmarked)
                repository.updateLesson(updated)
                if (updated.isBookmarked) {
                    repository.insertBookmark(
                        SavedBookmark(
                            itemType = "teacher_lesson",
                            itemId = lessonId,
                            title = updated.title,
                            subtitle = updated.category
                        )
                    )
                } else {
                    repository.deleteBookmark("teacher_lesson", lessonId)
                }
            }
        }
    }

    private suspend fun updateGlobalProgress() {
        // Simple update of parent Course progress percent
        _selectedCourseId.value?.let { courseId ->
            repository.getLessonsByCourse(courseId).collect { lessons ->
                if (lessons.isNotEmpty()) {
                    val completed = lessons.count { it.isCompleted }
                    val percent = (completed * 100) / lessons.size
                    repository.getCourseById(courseId)?.let { course ->
                        repository.updateCourse(course.copy(progressPercent = percent))
                    }
                }
            }
        }
    }

    // --- STUDENT SECTION STATE ---
    private val _studentClassLevel = MutableStateFlow("Nursery")
    val studentClassLevel: StateFlow<String> = _studentClassLevel.asStateFlow()

    private val _studentSubject = MutableStateFlow("English")
    val studentSubject: StateFlow<String> = _studentSubject.asStateFlow()

    val studentTopics: StateFlow<List<StudentTopic>> = combine(
        _studentClassLevel,
        _studentSubject
    ) { classLvl, sub ->
        var list = emptyList<StudentTopic>()
        repository.getStudentTopics(classLvl, sub).collect { list = it }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateStudentClassAndSubject(classLvl: String, sub: String) {
        _studentClassLevel.value = classLvl
        _studentSubject.value = sub
    }

    fun selectStudentTopic(topicId: Int) {
        navigateToWithBack(Screen.StudentTopicDetail(topicId))
    }

    fun toggleStudentTopicCompleted(topicId: Int) {
        viewModelScope.launch {
            repository.getStudentTopicById(topicId)?.let {
                val updated = it.copy(isCompleted = !it.isCompleted)
                repository.updateStudentTopic(updated)
            }
        }
    }

    fun toggleStudentTopicBookmarked(topicId: Int) {
        viewModelScope.launch {
            repository.getStudentTopicById(topicId)?.let {
                val updated = it.copy(isBookmarked = !it.isBookmarked)
                repository.updateStudentTopic(updated)
                if (updated.isBookmarked) {
                    repository.insertBookmark(
                        SavedBookmark(
                            itemType = "student_topic",
                            itemId = topicId,
                            title = updated.title,
                            subtitle = "${updated.classLevel} - ${updated.subject}"
                        )
                    )
                } else {
                    repository.deleteBookmark("student_topic", topicId)
                }
            }
        }
    }

    // --- STORIES & ACTIVITIES ---
    val stories: StateFlow<List<MoralStory>> = repository.allStories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedStoryCategory = MutableStateFlow("All")
    val selectedStoryCategory: StateFlow<String> = _selectedStoryCategory.asStateFlow()

    fun updateStoryCategory(cat: String) {
        _selectedStoryCategory.value = cat
    }

    fun selectStory(id: Int) {
        navigateToWithBack(Screen.StoryDetail(id))
    }

    fun toggleStoryBookmarked(storyId: Int) {
        viewModelScope.launch {
            var selectedStory: MoralStory? = null
            repository.allStories.collect { list ->
                selectedStory = list.find { it.id == storyId }
            }
            selectedStory?.let {
                val updated = it.copy(isBookmarked = !it.isBookmarked)
                repository.insertStory(updated) // updates story
                if (updated.isBookmarked) {
                    repository.insertBookmark(
                        SavedBookmark(
                            itemType = "story",
                            itemId = storyId,
                            title = updated.title,
                            subtitle = updated.category
                        )
                    )
                } else {
                    repository.deleteBookmark("story", storyId)
                }
            }
        }
    }

    // --- QUIZZES STATE ---
    val quizzes: StateFlow<List<SavedQuiz>> = repository.allQuizzes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeQuiz = MutableStateFlow<SavedQuiz?>(null)
    val activeQuiz: StateFlow<SavedQuiz?> = _activeQuiz.asStateFlow()

    private val _currentQuizQuestionIndex = MutableStateFlow(0)
    val currentQuizQuestionIndex: StateFlow<Int> = _currentQuizQuestionIndex.asStateFlow()

    private val _quizSelectedAnswerIndex = MutableStateFlow<Int?>(null)
    val quizSelectedAnswerIndex: StateFlow<Int?> = _quizSelectedAnswerIndex.asStateFlow()

    private val _quizCorrectAnswersCount = MutableStateFlow(0)
    val quizCorrectAnswersCount: StateFlow<Int> = _quizCorrectAnswersCount.asStateFlow()

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished: StateFlow<Boolean> = _quizFinished.asStateFlow()

    fun startQuiz(quiz: SavedQuiz) {
        _activeQuiz.value = quiz
        _currentQuizQuestionIndex.value = 0
        _quizSelectedAnswerIndex.value = null
        _quizCorrectAnswersCount.value = 0
        _quizFinished.value = false
        navigateToWithBack(Screen.StudentQuiz)
    }

    fun selectQuizAnswer(index: Int) {
        if (_quizSelectedAnswerIndex.value != null) return // Already answer selected
        _quizSelectedAnswerIndex.value = index
    }

    fun nextQuizQuestion(correctIndex: Int) {
        val selected = _quizSelectedAnswerIndex.value ?: return
        if (selected == correctIndex) {
            _quizCorrectAnswersCount.value += 1
        }

        val totalQuestions = _activeQuiz.value?.totalQuestions ?: 3
        if (_currentQuizQuestionIndex.value + 1 < totalQuestions) {
            _currentQuizQuestionIndex.value += 1
            _quizSelectedAnswerIndex.value = null
        } else {
            // Quiz completed
            _quizFinished.value = true
            viewModelScope.launch {
                _activeQuiz.value?.let { quiz ->
                    val finalScore = _quizCorrectAnswersCount.value
                    val updatedQuiz = quiz.copy(isCompleted = true, score = finalScore)
                    repository.updateQuiz(updatedQuiz)
                    // Update count of quizzes taken
                    val currentVal = repository.getStat("quizzes_taken")?.statValue ?: 0
                    repository.updateStat("quizzes_taken", currentVal + 1)
                }
            }
        }
    }

    // --- AI ASSISTANT STATE ---
    private val _aiRoleType = MutableStateFlow("Lesson Plan") // Worksheet, Homework, Question Paper, Report Card
    val aiRoleType: StateFlow<String> = _aiRoleType.asStateFlow()

    private val _aiSubject = MutableStateFlow("Math")
    val aiSubject: StateFlow<String> = _aiSubject.asStateFlow()

    private val _aiClassLevel = MutableStateFlow("Class 1")
    val aiClassLevel: StateFlow<String> = _aiClassLevel.asStateFlow()

    private val _aiTopicInput = MutableStateFlow("")
    val aiTopicInput: StateFlow<String> = _aiTopicInput.asStateFlow()

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _aiResponseLoading = MutableStateFlow(false)
    val aiResponseLoading: StateFlow<Boolean> = _aiResponseLoading.asStateFlow()

    val savedAiItems: StateFlow<List<AiGeneratedItem>> = repository.allAiItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateAiParameters(roleType: String, subject: String, classLvl: String, topic: String) {
        _aiRoleType.value = roleType
        _aiSubject.value = subject
        _aiClassLevel.value = classLvl
        _aiTopicInput.value = topic
    }

    fun generateAiContent() {
        val promptText = constructAiPrompt(
            roleType = _aiRoleType.value,
            subject = _aiSubject.value,
            classLvl = _aiClassLevel.value,
            topic = _aiTopicInput.value
        )
        _aiResponseLoading.value = true
        _aiResponse.value = ""

        viewModelScope.launch {
            try {
                val output = GeminiApiClient.generateContent(promptText)
                _aiResponse.value = output

                // Automatically save to Room on successful generation
                if (!output.startsWith("Error") && !output.startsWith("API Call Failed")) {
                    val titleSuffix = if (_aiTopicInput.value.isNotBlank()) _aiTopicInput.value else _aiSubject.value
                    repository.insertAiItem(
                        AiGeneratedItem(
                            type = _aiRoleType.value,
                            title = "${_aiRoleType.value}: $titleSuffix (${_aiClassLevel.value})",
                            prompt = _aiTopicInput.value,
                            content = output
                        )
                    )
                    val currentVal = repository.getStat("ai_generated_count")?.statValue ?: 0
                    repository.updateStat("ai_generated_count", currentVal + 1)
                }
            } catch (e: Exception) {
                _aiResponse.value = "Error generating content: ${e.localizedMessage}"
            } finally {
                _aiResponseLoading.value = false
            }
        }
    }

    fun deleteSavedAiItem(item: AiGeneratedItem) {
        viewModelScope.launch {
            repository.deleteAiItem(item)
        }
    }

    private fun constructAiPrompt(roleType: String, subject: String, classLvl: String, topic: String): String {
        return """
            You are Teacher Base Academy's Elite AI Educator, Curriculum Designer, and Child Psychologist.
            Generate a high-quality, practical, immediately usable $roleType for:
            - Target: $classLvl (Age appropriate guidelines)
            - Subject: $subject
            - Topic: ${if (topic.isNotBlank()) topic else "Fundamental foundational skills"}
            - Language Style: Bilingual Hindi-English (Hinglish guidelines where matching). Use rich examples, step-by-step interactive exercises, and custom training suggestions for beginner parents and tutors.
            
            Format the output strictly as markdown headers, with bold text instructions. Make it easy to read on mobile phones with clean negative space. Do not use complex HTML. 
            
            Ensure it includes:
            1. Clear Learning Objectives
            2. Step-by-Step Activities / Interactive exercises
            3. Detailed parent/tutor tips to teach clearly with visual/practical props
            4. Assessment/Questions to check understanding.
        """.trimIndent()
    }

    // --- ANALYTICS / STATS ---
    private val _teacherCompletedCount = MutableStateFlow(0)
    val teacherCompletedCount: StateFlow<Int> = _teacherCompletedCount.asStateFlow()

    private val _studentCompletedCount = MutableStateFlow(0)
    val studentCompletedCount: StateFlow<Int> = _studentCompletedCount.asStateFlow()

    private val _quizzesTakenCount = MutableStateFlow(0)
    val quizzesTakenCount: StateFlow<Int> = _quizzesTakenCount.asStateFlow()

    private val _aiCreatedCount = MutableStateFlow(0)
    val aiCreatedCount: StateFlow<Int> = _aiCreatedCount.asStateFlow()

    fun refreshStats() {
        viewModelScope.launch {
            _quizzesTakenCount.value = repository.getStat("quizzes_taken")?.statValue ?: 0
            _aiCreatedCount.value = repository.getStat("ai_generated_count")?.statValue ?: 0

            repository.allStudentTopics.collect { list ->
                _studentCompletedCount.value = list.count { it.isCompleted }
            }
        }
        viewModelScope.launch {
            // Count completed teacher lessons
            repository.allCourses.collect { list ->
                var lessonsCountTotal = 0
                list.forEach { course ->
                    repository.getLessonsByCourse(course.id).collect { lessons ->
                        lessonsCountTotal += lessons.count { it.isCompleted }
                    }
                }
                _teacherCompletedCount.value = lessonsCountTotal
            }
        }
    }

    init {
        refreshStats()
    }
}

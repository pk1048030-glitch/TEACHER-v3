package com.example.data

import kotlinx.coroutines.flow.Flow

class AcademyRepository(private val dao: AcademyDao) {
    // --- Teacher Training ---
    val allCourses: Flow<List<TeacherCourse>> = dao.getAllCourses()

    suspend fun getCourseById(id: Int): TeacherCourse? = dao.getCourseById(id)

    fun getLessonsByCourse(courseId: Int): Flow<List<TeacherLesson>> = dao.getLessonsByCourse(courseId)

    suspend fun getLessonById(lessonId: Int): TeacherLesson? = dao.getLessonById(lessonId)

    suspend fun updateLesson(lesson: TeacherLesson) = dao.updateLesson(lesson)

    suspend fun updateCourse(course: TeacherCourse) = dao.updateCourse(course)

    // --- Student Curriculum ---
    val allStudentTopics: Flow<List<StudentTopic>> = dao.getAllStudentTopics()

    fun getStudentTopics(classLevel: String, subject: String): Flow<List<StudentTopic>> =
        dao.getStudentTopics(classLevel, subject)

    suspend fun getStudentTopicById(topicId: Int): StudentTopic? = dao.getStudentTopicById(topicId)

    suspend fun updateStudentTopic(topic: StudentTopic) = dao.updateStudentTopic(topic)

    // --- Stories ---
    val allStories: Flow<List<MoralStory>> = dao.getAllStories()

    fun getStoriesByCategory(category: String): Flow<List<MoralStory>> = dao.getStoriesByCategory(category)

    suspend fun insertStory(story: MoralStory) = dao.insertStory(story)

    // --- Quizzes ---
    val allQuizzes: Flow<List<SavedQuiz>> = dao.getAllQuizzes()

    fun getQuizzes(classLevel: String, subject: String): Flow<List<SavedQuiz>> = dao.getQuizzes(classLevel, subject)

    suspend fun updateQuiz(quiz: SavedQuiz) = dao.updateQuiz(quiz)

    suspend fun insertQuiz(quiz: SavedQuiz) = dao.insertQuiz(quiz)

    // --- AI Generated Items ---
    val allAiItems: Flow<List<AiGeneratedItem>> = dao.getAllAiItems()

    suspend fun insertAiItem(item: AiGeneratedItem) = dao.insertAiItem(item)

    suspend fun deleteAiItem(item: AiGeneratedItem) = dao.deleteAiItem(item)

    // --- Bookmarks ---
    val allBookmarks: Flow<List<SavedBookmark>> = dao.getAllBookmarks()

    suspend fun insertBookmark(bookmark: SavedBookmark) = dao.insertBookmark(bookmark)

    suspend fun deleteBookmark(itemType: String, itemId: Int) = dao.deleteBookmark(itemType, itemId)

    // --- Stats ---
    suspend fun getStat(key: String): AppStat? = dao.getStat(key)

    suspend fun updateStat(key: String, value: Int) {
        dao.insertStat(AppStat(key, value))
    }
}

package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademyDao {
    // --- Teacher Training ---
    @Query("SELECT * FROM teacher_courses ORDER BY id ASC")
    fun getAllCourses(): Flow<List<TeacherCourse>>

    @Query("SELECT * FROM teacher_courses WHERE id = :id")
    suspend fun getCourseById(id: Int): TeacherCourse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: TeacherCourse): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<TeacherCourse>)

    @Update
    suspend fun updateCourse(course: TeacherCourse)

    @Query("SELECT * FROM teacher_lessons WHERE courseId = :courseId ORDER BY id ASC")
    fun getLessonsByCourse(courseId: Int): Flow<List<TeacherLesson>>

    @Query("SELECT * FROM teacher_lessons WHERE id = :lessonId")
    suspend fun getLessonById(lessonId: Int): TeacherLesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: TeacherLesson): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<TeacherLesson>)

    @Update
    suspend fun updateLesson(lesson: TeacherLesson)

    // --- Student Curriculum ---
    @Query("SELECT * FROM student_topics ORDER BY id ASC")
    fun getAllStudentTopics(): Flow<List<StudentTopic>>

    @Query("SELECT * FROM student_topics WHERE classLevel = :classLevel AND subject = :subject ORDER BY id ASC")
    fun getStudentTopics(classLevel: String, subject: String): Flow<List<StudentTopic>>

    @Query("SELECT * FROM student_topics WHERE id = :topicId")
    suspend fun getStudentTopicById(topicId: Int): StudentTopic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentTopic(topic: StudentTopic): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentTopics(topics: List<StudentTopic>)

    @Update
    suspend fun updateStudentTopic(topic: StudentTopic)

    // --- Moral Stories ---
    @Query("SELECT * FROM moral_stories ORDER BY id ASC")
    fun getAllStories(): Flow<List<MoralStory>>

    @Query("SELECT * FROM moral_stories WHERE category = :category ORDER BY id ASC")
    fun getStoriesByCategory(category: String): Flow<List<MoralStory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: MoralStory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<MoralStory>)

    @Update
    suspend fun updateStory(story: MoralStory)

    // --- Saved Quizzes ---
    @Query("SELECT * FROM quizzes ORDER BY id DESC")
    fun getAllQuizzes(): Flow<List<SavedQuiz>>

    @Query("SELECT * FROM quizzes WHERE classLevel = :classLevel AND subject = :subject ORDER BY id DESC")
    fun getQuizzes(classLevel: String, subject: String): Flow<List<SavedQuiz>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: SavedQuiz): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<SavedQuiz>)

    @Update
    suspend fun updateQuiz(quiz: SavedQuiz)

    // --- AI Generated Items ---
    @Query("SELECT * FROM ai_generated_items ORDER BY createdAt DESC")
    fun getAllAiItems(): Flow<List<AiGeneratedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiItem(item: AiGeneratedItem): Long

    @Delete
    suspend fun deleteAiItem(item: AiGeneratedItem)

    // --- Bookmarks ---
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<SavedBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: SavedBookmark): Long

    @Query("DELETE FROM bookmarks WHERE itemType = :itemType AND itemId = :itemId")
    suspend fun deleteBookmark(itemType: String, itemId: Int)

    // --- Stats ---
    @Query("SELECT * FROM app_stats WHERE statKey = :key")
    suspend fun getStat(key: String): AppStat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: AppStat)
}

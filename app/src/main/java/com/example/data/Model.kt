package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teacher_courses")
data class TeacherCourse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // Child Psychology, Classroom Management, Lesson Planning, Teaching Skills, Communication Skills, Behaviour Management, Public Speaking, Teacher Ethics, Confidence Building
    val progressPercent: Int = 0,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "teacher_lessons")
data class TeacherLesson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val title: String,
    val category: String,
    val explanation: String,
    val realExample: String,
    val classroomActivity: String,
    val teachingTips: String,
    val assessmentQuestion: String,
    val assessmentAnswer: String,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "student_topics")
data class StudentTopic(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val classLevel: String, // Nursery, LKG, UKG, Class 1, Class 2, Class 3, Class 4, Class 5
    val subject: String, // Hindi, English, Math, EVS, Science, Social Science, GK, Reasoning, Computer, Moral Education
    val title: String,
    val subtopic: String,
    val content: String,
    val examplesJson: String, // Simple list of examples / Q&A
    val exercisesJson: String, // Quiz questions for practice
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "moral_stories")
data class MoralStory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val titleHindi: String,
    val content: String,
    val contentHindi: String,
    val category: String, // Panchatantra, Moral, Inspirational, Folklore
    val moral: String,
    val moralHindi: String,
    val readingTimeMinutes: Int = 3,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "quizzes")
data class SavedQuiz(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val classLevel: String,
    val subject: String,
    val questionsJson: String, // Serialized array of Questions
    val difficulty: String = "Medium", // Easy, Medium, Hard
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val totalQuestions: Int = 5
)

@Entity(tableName = "ai_generated_items")
data class AiGeneratedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // Lesson Plan, Worksheet, Homework, Question Paper, Report Card, Classroom Activity
    val title: String,
    val prompt: String,
    val content: String, // Generated text/markdown content
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class SavedBookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemType: String, // teacher_lesson, student_topic, story, general
    val itemId: Int,
    val title: String,
    val subtitle: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_stats")
data class AppStat(
    @PrimaryKey val statKey: String, // e.g., "teacher_progress", "student_progress", "quizzes_taken", "ai_generated_count"
    val statValue: Int
)

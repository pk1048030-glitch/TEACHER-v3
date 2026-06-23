package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import kotlinx.coroutines.launch

// Line representing points in Drawing Canvas
data class DrawPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyMainShell(viewModel: AcademyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val bottomNavTab = when (currentScreen) {
        is Screen.TeacherHome, is Screen.TeacherCourseDetail, is Screen.TeacherLessonDetail -> 0
        is Screen.StudentHome, is Screen.StudentTopicDetail, is Screen.StudentQuiz -> 1
        is Screen.AiAssistant -> 2
        is Screen.StoryAndActivity, is Screen.StoryDetail, is Screen.DrawingCanvas -> 3
        is Screen.Analytics -> 4
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "App Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                "Teacher Base",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Academy • शिक्षक साथी",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (currentScreen != Screen.TeacherHome && 
                        currentScreen != Screen.StudentHome && 
                        currentScreen != Screen.AiAssistant && 
                        currentScreen != Screen.StoryAndActivity && 
                        currentScreen != Screen.Analytics) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back Button"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "📚 Academy Offline Database Status: Active & Synced", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Sync Status",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3F0F5),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = bottomNavTab == 0,
                    onClick = { viewModel.navigateTo(Screen.TeacherHome) },
                    icon = { Icon(imageVector = if (bottomNavTab == 0) Icons.Filled.SupervisorAccount else Icons.Outlined.SupervisorAccount, contentDescription = "Teacher") },
                    label = { Text("Teacher (शिक्षक)", fontSize = 10.sp, maxLines = 1) },
                    modifier = Modifier.testTag("nav_tab_teacher")
                )
                NavigationBarItem(
                    selected = bottomNavTab == 1,
                    onClick = { viewModel.navigateTo(Screen.StudentHome) },
                    icon = { Icon(imageVector = if (bottomNavTab == 1) Icons.Filled.Class else Icons.Outlined.Class, contentDescription = "Student") },
                    label = { Text("Kids (बच्चे)", fontSize = 10.sp, maxLines = 1) },
                    modifier = Modifier.testTag("nav_tab_student")
                )
                NavigationBarItem(
                    selected = bottomNavTab == 2,
                    onClick = { viewModel.navigateTo(Screen.AiAssistant) },
                    icon = { Icon(imageVector = if (bottomNavTab == 2) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome, contentDescription = "AI") },
                    label = { Text("AI Assistant", fontSize = 10.sp, maxLines = 1) },
                    modifier = Modifier.testTag("nav_tab_ai")
                )
                NavigationBarItem(
                    selected = bottomNavTab == 3,
                    onClick = { viewModel.navigateTo(Screen.StoryAndActivity) },
                    icon = { Icon(imageVector = if (bottomNavTab == 3) Icons.Filled.MenuBook else Icons.Outlined.MenuBook, contentDescription = "Learn") },
                    label = { Text("Play (कहानी)", fontSize = 10.sp, maxLines = 1) },
                    modifier = Modifier.testTag("nav_tab_learn")
                )
                NavigationBarItem(
                    selected = bottomNavTab == 4,
                    onClick = { 
                        viewModel.refreshStats()
                        viewModel.navigateTo(Screen.Analytics) 
                    },
                    icon = { Icon(imageVector = if (bottomNavTab == 4) Icons.Filled.Analytics else Icons.Outlined.Analytics, contentDescription = "Analytics") },
                    label = { Text("Stats (रिपोर्ट)", fontSize = 10.sp, maxLines = 1) },
                    modifier = Modifier.testTag("nav_tab_stats")
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.TeacherHome -> TeacherHomeScreen(viewModel)
                    is Screen.TeacherCourseDetail -> TeacherCourseDetailScreen(viewModel, screen.courseId)
                    is Screen.TeacherLessonDetail -> TeacherLessonDetailScreen(viewModel, screen.lessonId)
                    Screen.StudentHome -> StudentHomeScreen(viewModel)
                    is Screen.StudentTopicDetail -> StudentTopicDetailScreen(viewModel, screen.topicId)
                    Screen.StudentQuiz -> StudentQuizScreen(viewModel)
                    Screen.AiAssistant -> AiAssistantScreen(viewModel)
                    Screen.StoryAndActivity -> StoryAndActivityScreen(viewModel)
                    is Screen.StoryDetail -> StoryDetailScreen(viewModel, screen.storyId)
                    Screen.DrawingCanvas -> DrawingCanvasScreen(viewModel)
                    Screen.Analytics -> AnalyticsScreen(viewModel)
                }
            }
        }
    }
}

// ----------------------------------------------------
// 1. TEACHER HUB SCREENS
// ----------------------------------------------------
@Composable
fun TeacherHomeScreen(viewModel: AcademyViewModel) {
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Teacher Training Academy 🎓",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "अपना आत्मविश्वास बढ़ाएं! Become a highly skilled and confident primary teacher with expert pedagogical modules in Child Psychology, Classroom Management, and more.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Beginner to Pro Method", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                "Training Courses (प्रशिक्षण पाठ्यक्रम)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (courses.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else {
            items(courses) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectCourse(course.id) }
                        .testTag("course_item_${course.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when(course.category) {
                                        "Child Psychology" -> Icons.Default.Psychology
                                        "Classroom Management" -> Icons.Default.Groups
                                        "Lesson Planning" -> Icons.Default.Assignment
                                        "Communication Skills" -> Icons.Default.RecordVoiceOver
                                        else -> Icons.Default.SupervisedUserCircle
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    course.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    course.category,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            IconButton(onClick = { /* Handle bookmark from course detail */ }) {
                                Icon(
                                    imageVector = if (course.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (course.isBookmarked) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            course.description,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = course.progressPercent / 100f,
                                modifier = Modifier.weight(1f).clip(CircleShape).height(6.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${course.progressPercent}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherCourseDetailScreen(viewModel: AcademyViewModel, courseId: Int) {
    val lessons by viewModel.courseLessons.collectAsStateWithLifecycle()
    var courseTitle by remember { mutableStateOf("Course Details") }

    LaunchedEffect(courseId) {
        viewModel.courses.value.find { it.id == courseId }?.let {
            courseTitle = it.title
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                courseTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Complete each lesson to complete the course certification. (पाठ्यक्रम पूर्ण करें)",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (lessons.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No lessons available offline at the moment.", color = Color.Gray)
                }
            }
        } else {
            items(lessons) { lesson ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectLesson(lesson.id) }
                        .testTag("lesson_card_${lesson.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, color = if(lesson.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha=0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        if (lesson.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (lesson.isCompleted) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        "${lessons.indexOf(lesson) + 1}",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    lesson.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Training Lesson",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            IconButton(onClick = { viewModel.toggleLessonBookmarked(lesson.id) }) {
                                Icon(
                                    imageVector = if (lesson.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (lesson.isBookmarked) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherLessonDetailScreen(viewModel: AcademyViewModel, lessonId: Int) {
    var lesson by remember { mutableStateOf<TeacherLesson?>(null) }
    var selectedTab by remember { mutableStateOf(0) }
    var userAssessmentAnswerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(lessonId) {
        viewModel.courseLessons.value.find { it.id == lessonId }?.let {
            lesson = it
        }
    }

    if (lesson == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentLesson = lesson!!

    Column(modifier = Modifier.fillMaxSize()) {
        // Lesson Header Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            Text(
                currentLesson.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(
                    onClick = { },
                    label = { Text(currentLesson.category) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.toggleLessonBookmarked(currentLesson.id) }) {
                    Icon(
                        imageVector = if (currentLesson.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (currentLesson.isBookmarked) MaterialTheme.colorScheme.primary else Color.DarkGray
                    )
                }
            }
        }

        // Horizontal Category Tabs
        val tabTitles = listOf("1. समझें (Info)", "2. उदाहरण (Example)", "3. एक्टिविटी (Activity)", "4. टिप्स (Tips)", "5. परिक्षण (Test)")
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { 
                        selectedTab = index 
                        if (index == 4) userAssessmentAnswerVisible = false
                    },
                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab Contents
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> { // Explanation
                    Text(
                        "Concept Explanation (व्याख्या):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        currentLesson.explanation,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                1 -> { // Example
                    Text(
                        "Real-world Demonstration Example (वास्तविक उदाहरण):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha=0.3f))
                    ) {
                        Text(
                            currentLesson.realExample,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                2 -> { // Classroom Activity
                    Text(
                        "Classroom Interactive Activity (कक्षा की गतिविधि):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        "Try to run this practical task in your classroom to test comprehension:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        currentLesson.classroomActivity,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                3 -> { // Teaching Tips
                    Text(
                        "Aesthetic Pedagogical Tips (शिक्षण युक्तियाँ):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Icon(Icons.Outlined.Lightbulb, contentDescription = "Tip Icon", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                currentLesson.teachingTips,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                4 -> { // Assessment
                    Text(
                        "Self Assessment Question (कक्षा प्रश्न):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                currentLesson.assessmentQuestion,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            if (!userAssessmentAnswerVisible) {
                                Button(
                                    onClick = { userAssessmentAnswerVisible = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Reveal Correct Answer (उत्तर देखें)", fontSize = 12.sp)
                                }
                            } else {
                                Text(
                                    "Correct Answer: ${currentLesson.assessmentAnswer}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Action Button to Complete
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLesson.isCompleted) "Completed! ✅" else "In Progress...",
                    fontWeight = FontWeight.Bold,
                    color = if (currentLesson.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontSize = 14.sp
                )
                Button(
                    onClick = { 
                        viewModel.toggleLessonCompleted(currentLesson.id)
                        // Trigger simple state modification locally
                        lesson = currentLesson.copy(isCompleted = !currentLesson.isCompleted)
                    },
                    modifier = Modifier.testTag("mark_complete_button")
                ) {
                    Text(
                        text = if (currentLesson.isCompleted) "Mark Incomplete" else "Mark Complete (पूर्ण हुआ)",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 2. STUDENT CURRICULUM SECTIONS
// ----------------------------------------------------
@Composable
fun StudentHomeScreen(viewModel: AcademyViewModel) {
    val activeClass by viewModel.studentClassLevel.collectAsStateWithLifecycle()
    val activeSubject by viewModel.studentSubject.collectAsStateWithLifecycle()
    val topics by viewModel.studentTopics.collectAsStateWithLifecycle()
    val quizzesList by viewModel.quizzes.collectAsStateWithLifecycle()

    val classes = listOf("Nursery", "LKG", "UKG", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5")
    val subjects = listOf("Hindi", "English", "Math", "Science", "GK", "Reasoning")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Classes list (Horizontal scroll filter)
        item {
            Text(
                "Select Child Class Level (कक्षा का चयन):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                classes.forEach { classLvl ->
                    val isSelected = activeClass == classLvl
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateStudentClassAndSubject(classLvl, activeSubject) },
                        label = { Text(classLvl, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("class_chip_${classLvl.replace(" ", "_")}")
                    )
                }
            }
        }

        // Subjects list (Horizontal scroll filter)
        item {
            Text(
                "Select Subject (विषय का चयन):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjects.forEach { sub ->
                    val isSelected = activeSubject == sub
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateStudentClassAndSubject(activeClass, sub) },
                        label = { Text(sub, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier.testTag("subject_chip_$sub")
                    )
                }
            }
        }

        // Quick Quizzes Card Header
        item {
            val classQuiz = quizzesList.filter { 
                it.classLevel.equals(activeClass, ignoreCase = true) || 
                it.classLevel == "Teacher" || 
                it.title.contains(activeClass, ignoreCase = true)
            }
            if (classQuiz.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.startQuiz(classQuiz.first()) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.tertiary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Quiz, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${activeClass} Quick Quiz 🎮",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                "Take a fast offline test to boost child confidence!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Button(
                            onClick = { viewModel.startQuiz(classQuiz.first()) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Text("Play", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            Text(
                "Learning Lessons (${topics.size} Topics found):",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (topics.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "We are ready to generate more offline curriculum!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "You can generate bespoke syllabus topics offline instantly using our AI Assistant!",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(topics) { topic ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectStudentTopic(topic.id) }
                        .testTag("student_topic_${topic.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, color = if(topic.isCompleted) MaterialTheme.colorScheme.secondary.copy(alpha=0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (topic.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha=0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if(topic.isCompleted) Icons.Default.CheckCircle else Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = if(topic.isCompleted) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                topic.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Subtopic: ${topic.subtopic}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        IconButton(onClick = { viewModel.toggleStudentTopicBookmarked(topic.id) }) {
                            Icon(
                                imageVector = if (topic.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (topic.isBookmarked) MaterialTheme.colorScheme.secondary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentTopicDetailScreen(viewModel: AcademyViewModel, topicId: Int) {
    var topic by remember { mutableStateOf<StudentTopic?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(topicId) {
        viewModel.studentTopics.value.find { it.id == topicId }?.let {
            topic = it
        }
    }

    if (topic == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentTopic = topic!!

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp)
        ) {
            Text(
                currentTopic.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                "${currentTopic.classLevel} • ${currentTopic.subject}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Learn Today (आज की पढ़ाई):",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            // Beautiful Box wrapping the main core curriculum topic lesson info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        currentTopic.content,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                "Interactive Practice Questions:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Teachers & Tutors checklist: Have the child read aloud, write these on a notebook, and select matching responses.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "1. Which letter sounds like the core word A?\n2. Match correct elements on screen.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Action complete footer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentTopic.isCompleted) "Topic Mastered! ⭐" else "Let's learn and master!",
                    fontWeight = FontWeight.Bold,
                    color = if (currentTopic.isCompleted) MaterialTheme.colorScheme.secondary else Color.Gray,
                    fontSize = 13.sp
                )
                Button(
                    onClick = { 
                        viewModel.toggleStudentTopicCompleted(currentTopic.id)
                        topic = currentTopic.copy(isCompleted = !currentTopic.isCompleted)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.testTag("mark_student_topic_complete")
                ) {
                    Text(
                        text = if (currentTopic.isCompleted) "Keep Learning" else "Mastered (पूर्ण हुआ)",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. QUIZ SCREEN IMPLEMENTATION
// ----------------------------------------------------
@Composable
fun StudentQuizScreen(viewModel: AcademyViewModel) {
    val activeQuiz by viewModel.activeQuiz.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuizQuestionIndex.collectAsStateWithLifecycle()
    val selectedAnsIndex by viewModel.quizSelectedAnswerIndex.collectAsStateWithLifecycle()
    val correctCount by viewModel.quizCorrectAnswersCount.collectAsStateWithLifecycle()
    val finished by viewModel.quizFinished.collectAsStateWithLifecycle()

    if (activeQuiz == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No quiz standard found.", color = Color.Gray)
        }
        return
    }

    val quizObj = activeQuiz!!
    
    // Simplistic parsing of JSON questions: we'll define static representations
    // to bypass serialization runtime dependencies
    val isDemoQuiz = quizObj.title.contains("Psychology")
    val questionText = if (isDemoQuiz) {
        when (currentIndex) {
            0 -> "What learning style involves learning by doing?"
            1 -> "Praising effort instead of smartness creates a..."
            else -> "Specific feedback is better than general praise."
        }
    } else if (quizObj.title.contains("Nursery")) {
        when (currentIndex) {
            0 -> "What starts with the letter 'अ'?"
            1 -> "How many fingers are there on one hand?"
            else -> "Complete: 1, 2, 3, _, 5"
        }
    } else {
        when (currentIndex) {
            0 -> "What is 4 + 3?"
            1 -> "True or False: 2 + 5 equals 8."
            else -> "If you have 5 baloons and get 1 more, how many do you have?"
        }
    }

    val optionsList = if (isDemoQuiz) {
        when (currentIndex) {
            0 -> listOf("Auditory", "Visual", "Kinesthetic", "Abstract")
            1 -> listOf("Fixed Mindset", "Growth Mindset", "Egoistic Mindset", "None")
            else -> listOf("True", "False")
        }
    } else if (quizObj.title.contains("Nursery")) {
        when (currentIndex) {
            0 -> listOf("आम", "अनार", "इमली", "मछली")
            1 -> listOf("3", "4", "5", "6")
            else -> listOf("4", "6", "0", "9")
        }
    } else {
        when (currentIndex) {
            0 -> listOf("6", "7", "8", "9")
            1 -> listOf("True", "False")
            else -> listOf("4", "6", "5", "7")
        }
    }

    val ansIndex = if (isDemoQuiz) {
        when (currentIndex) {
            0 -> 2
            1 -> 1
            else -> 0
        }
    } else if (quizObj.title.contains("Nursery")) {
        when (currentIndex) {
            0 -> 1
            1 -> 2
            else -> 0
        }
    } else {
        when (currentIndex) {
            0 -> 1
            1 -> 1
            else -> 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!finished) {
            // Heading with status indicators
            Text(
                quizObj.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                "Level: ${quizObj.difficulty} • Question ${currentIndex + 1} of ${quizObj.totalQuestions}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LinearProgressIndicator(
                progress = (currentIndex + 1).toFloat() / quizObj.totalQuestions,
                modifier = Modifier.fillMaxWidth().clip(CircleShape).height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Question Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
            ) {
                Text(
                    text = questionText,
                    modifier = Modifier.padding(24.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Answer choices
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                optionsList.forEachIndexed { optIndex, text ->
                    val isSelected = selectedAnsIndex == optIndex
                    val isAnswered = selectedAnsIndex != null
                    val activeColor = when {
                        !isAnswered -> MaterialTheme.colorScheme.surface
                        isSelected && optIndex == ansIndex -> Color(0xFF22C55E) // Green correct
                        isSelected && optIndex != ansIndex -> Color(0xFFEF4444) // Red wrong
                        optIndex == ansIndex -> Color(0xFF22C55E) // Reveal correct also
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectQuizAnswer(optIndex) }
                            .testTag("quiz_option_$optIndex"),
                        colors = CardDefaults.cardColors(containerColor = activeColor),
                        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.selectQuizAnswer(optIndex) },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = text,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAnswered && (optIndex == ansIndex || isSelected)) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            if (selectedAnsIndex != null) {
                Button(
                    onClick = { viewModel.nextQuizQuestion(ansIndex) },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("quiz_next_button")
                ) {
                    Text("Next Question (अगला प्रश्न)", fontSize = 14.sp)
                }
            }
        } else {
            // Finished block success stats
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Quiz Completed! (परीक्षा पूर्ण)",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Congratulations! You did a wonderful job. Keep scoring high and learning.",
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("YOUR FINAL SCORE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "$correctCount / ${quizObj.totalQuestions}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Return to Classroom", fontSize = 14.sp)
            }
        }
    }
}

// ----------------------------------------------------
// 4. AI TEACHING ASSISTANT & WORKSHEET GENERATOR
// ----------------------------------------------------
@Composable
fun AiAssistantScreen(viewModel: AcademyViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val aiRole by viewModel.aiRoleType.collectAsStateWithLifecycle()
    val aiSubject by viewModel.aiSubject.collectAsStateWithLifecycle()
    val aiClassLevel by viewModel.aiClassLevel.collectAsStateWithLifecycle()
    val aiTopic by viewModel.aiTopicInput.collectAsStateWithLifecycle()

    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    val isLoading by viewModel.aiResponseLoading.collectAsStateWithLifecycle()
    val savedItems by viewModel.savedAiItems.collectAsStateWithLifecycle()

    val roles = listOf("Lesson Plan", "Worksheet", "Homework", "Question Paper", "Report Card")
    val subjects = listOf("Hindi", "English", "Math", "Science", "GK", "Reasoning")
    val classes = listOf("Nursery", "LKG", "UKG", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5")

    var isSavedTab by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = if (isSavedTab) 1 else 0) {
            Tab(selected = !isSavedTab, onClick = { isSavedTab = false }) {
                Text("AI Generator 🤖", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = isSavedTab, onClick = { isSavedTab = true }) {
                Text("Saved Resources 💾", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        if (!isSavedTab) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "AI Teaching Assistant (शिक्षक सहायक)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Enter criteria below to generate tailored interactive lessons, homework checklists, worksheets, and custom schedules instantly using the Gemini AI Model.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Options fields
                Text("1. What do you want to generate?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    roles.forEach { role ->
                        FilterChip(
                            selected = aiRole == role,
                            onClick = { viewModel.updateAiParameters(role, aiSubject, aiClassLevel, aiTopic) },
                            label = { Text(role) }
                        )
                    }
                }

                Text("2. Target Class / Age level:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    classes.forEach { lvl ->
                        FilterChip(
                            selected = aiClassLevel == lvl,
                            onClick = { viewModel.updateAiParameters(aiRole, aiSubject, lvl, aiTopic) },
                            label = { Text(lvl) }
                        )
                    }
                }

                Text("3. Subject & Topic parameters:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    subjects.forEach { sub ->
                        FilterChip(
                            selected = aiSubject == sub,
                            onClick = { viewModel.updateAiParameters(aiRole, sub, aiClassLevel, aiTopic) },
                            label = { Text(sub) }
                        )
                    }
                }

                OutlinedTextField(
                    value = aiTopic,
                    onValueChange = { viewModel.updateAiParameters(aiRole, aiSubject, aiClassLevel, it) },
                    label = { Text("E.g: Add fractions, Swar sound 'आ', Animals name, Solar system") },
                    modifier = Modifier.fillMaxWidth().testTag("ai_topic_input"),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Button(
                    onClick = { viewModel.generateAiContent() },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("generate_ai_btn"),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("AI Generating... Please wait (60s Limit)", fontSize = 12.sp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate via Gemini 3.5 Flash 🚀", fontSize = 13.sp)
                        }
                    }
                }

                if (aiResponse.isNotBlank()) {
                    Text("Generated Content Outline:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Toolbar inside card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(aiResponse))
                                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                }
                            }
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = aiResponse,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Saved Resources list
            if (savedItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(48.dp), tint=Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No saved worksheets or plans found.", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            var expandedItem by remember { mutableStateOf(false) }
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Saved Offline • ${java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(java.util.Date(item.createdAt))}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    IconButton(onClick = { viewModel.deleteSavedAiItem(item) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                if (expandedItem) {
                                    Text(
                                        text = item.content,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 16.sp,
                                        modifier = Modifier
                                            .background(Color.Gray.copy(alpha = 0.05f))
                                            .padding(12.dp)
                                            .fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = { expandedItem = false }) {
                                            Text("Collapse")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(onClick = {
                                            clipboardManager.setText(AnnotatedString(item.content))
                                            Toast.makeText(context, "Copied content!", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Text("Copy Outline", fontSize = 11.sp)
                                        }
                                    }
                                } else {
                                    TextButton(onClick = { expandedItem = true }) {
                                        Text("Expand Content Outline")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. STORY LIBRARY & ACTIVITY CANVAS HUB
// ----------------------------------------------------
@Composable
fun StoryAndActivityScreen(viewModel: AcademyViewModel) {
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val activeCat by viewModel.selectedStoryCategory.collectAsStateWithLifecycle()

    var activePlayTab by remember { mutableStateOf(0) } // 0: Story library, 1: Activity Hub

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = activePlayTab) {
            Tab(selected = activePlayTab == 0, onClick = { activePlayTab = 0 }) {
                Text("Moral Stories 📖", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activePlayTab == 1, onClick = { activePlayTab = 1 }) {
                Text("Activities 🎨", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        if (activePlayTab == 0) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Category Filter Scroll list
                val cats = listOf("All", "Moral", "Panchatantra", "Folklore")
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cats.forEach { cat ->
                        FilterChip(
                            selected = activeCat == cat,
                            onClick = { viewModel.updateStoryCategory(cat) },
                            label = { Text(cat) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                val filteredStories = if (activeCat == "All") stories else stories.filter { it.category == activeCat }

                if (filteredStories.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No offline stories stored in DB.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredStories) { story ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectStory(story.id) }
                                    .testTag("story_${story.id}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(story.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(story.titleHindi, fontSize = 13.sp, color = Color.Gray)
                                    }
                                    IconButton(onClick = { viewModel.toggleStoryBookmarked(story.id) }) {
                                        Icon(
                                            imageVector = if (story.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (story.isBookmarked) MaterialTheme.colorScheme.secondary else Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Interactive Activities lists & Drawing canvas launcher
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Drawing Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(Screen.DrawingCanvas) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Kids Drawing Canvas 🎨", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Launch offline fluid, responsive whiteboard! Express creativity with brush sizes, multiple markers, and instant clear commands.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.navigateTo(Screen.DrawingCanvas) }) {
                                Text("Launch Sandbox", fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Text("Simple Science Experiments (विज्ञान के प्रयोग):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Experiments list
                val experiments = listOf(
                    "Walking Water Experiment" to "Requires 3 jars, towels, food dye. Shows kids capillary siphon actions physically.",
                    "Magic Pepper Experiment" to "Requires plate of water, pepper, dish detergent. Explains surface tension of liquid.",
                    "Lemon Volcano" to "Lemon slice, baking soda, paint. Produces beautiful fizzing reactions in 2 minutes!"
                )

                items(experiments) { (title, desc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Outlined.Science, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(desc, fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoryDetailScreen(viewModel: AcademyViewModel, storyId: Int) {
    var story by remember { mutableStateOf<MoralStory?>(null) }
    var selectedLanguageIsHindi by remember { mutableStateOf(false) }

    LaunchedEffect(storyId) {
        viewModel.stories.value.find { it.id == storyId }?.let {
            story = it
        }
    }

    if (story == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val storyObj = story!!

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (selectedLanguageIsHindi) storyObj.titleHindi else storyObj.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    "Story Library • Category: ${storyObj.category}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            IconButton(onClick = { viewModel.toggleStoryBookmarked(storyObj.id) }) {
                Icon(
                    imageVector = if (storyObj.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (storyObj.isBookmarked) MaterialTheme.colorScheme.secondary else Color.DarkGray
                )
            }
        }

        // Language toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text("Eng", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterVertically))
            Switch(
                checked = selectedLanguageIsHindi,
                onCheckedChange = { selectedLanguageIsHindi = it },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text("हिंदी", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterVertically))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = if (selectedLanguageIsHindi) storyObj.contentHindi else storyObj.content,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Moral panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "MORAL LESSON (सीख):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedLanguageIsHindi) storyObj.moralHindi else storyObj.moral,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 6. DRAWING CANVAS IMPLEMENTATION
// ----------------------------------------------------
@Composable
fun DrawingCanvasScreen(viewModel: AcademyViewModel) {
    var paths = remember { mutableStateListOf<DrawPath>() }
    var currentPoints = remember { mutableStateListOf<Offset>() }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableStateOf(8f) }

    val colors = listOf(Color.Black, Color.Red, Color(0xFF0F766E), Color(0xFF1E3A8A), Color(0xFFD97706), Color(0xFFFF22BB))

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kids Art Canvas 🎨", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Doodle below using finger strokes. Highly responsive and clean.", fontSize = 11.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(10.dp))

        // Toolbar for colors and brush
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                colors.forEach { color ->
                    val isSelected = selectedColor == color
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(color, shape = CircleShape)
                            .border(
                                2.dp,
                                if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                CircleShape
                            )
                            .clickable { selectedColor = color }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    paths.clear()
                    currentPoints.clear()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.Red)
                }
                Button(onClick = { viewModel.navigateBack() }) {
                    Text("Close", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Canvas element
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .border(1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), shape = RoundedCornerShape(24.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPoints.add(offset)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            currentPoints.add(change.position)
                        },
                        onDragEnd = {
                            paths.add(DrawPath(currentPoints.toList(), selectedColor, brushSize))
                            currentPoints.clear()
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw historic strokes
                paths.forEach { drawPath ->
                    val path = Path().apply {
                        if (drawPath.points.isNotEmpty()) {
                            moveTo(drawPath.points.first().x, drawPath.points.first().y)
                            for (i in 1 until drawPath.points.size) {
                                lineTo(drawPath.points[i].x, drawPath.points[i].y)
                            }
                        }
                    }
                    drawPath(
                        path = path,
                        color = drawPath.color,
                        style = Stroke(width = drawPath.strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Draw active stroke
                if (currentPoints.isNotEmpty()) {
                    val activePath = Path().apply {
                        moveTo(currentPoints.first().x, currentPoints.first().y)
                        for (i in 1 until currentPoints.size) {
                            lineTo(currentPoints[i].x, currentPoints[i].y)
                        }
                    }
                    drawPath(
                        path = activePath,
                        color = selectedColor,
                        style = Stroke(width = brushSize, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 7. ANALYTICS & DASHBOARD REPORTS
// ----------------------------------------------------
@Composable
fun AnalyticsScreen(viewModel: AcademyViewModel) {
    val teacherVal by viewModel.teacherCompletedCount.collectAsStateWithLifecycle()
    val studentVal by viewModel.studentCompletedCount.collectAsStateWithLifecycle()
    val quizzesCount by viewModel.quizzesTakenCount.collectAsStateWithLifecycle()
    val aiCount by viewModel.aiCreatedCount.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarksState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Learning Analytics (प्रगति रिपोर्ट)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Track lessons, certified completions, and quiz statistics offline.",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        // Stats boxes grid
        item {
            val stats = listOf(
                Pair("Teacher Lessons", "$teacherVal Completed"),
                Pair("Kids Lessons", "$studentVal Mastered"),
                Pair("Total Quizzes", "$quizzesCount Attempts"),
                Pair("AI Materials", "$aiCount Created")
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().height(160.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(stats) { (key, value) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(key, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Custom canvas progression block charting strong vs weak subjects
        item {
            Text(
                "Subject Performance Overview:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Foundational Strengths Chart", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))

                    val chartItems = listOf("Hindi Swar 90%", "English Phonics 75%", "Math Addition 85%", "EVS Science 60%")
                    chartItems.forEach { item ->
                        val text = item.substringBefore(" ")
                        val pct = item.substringAfter(" ").replace("%","").toInt() / 100f
                        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text("${(pct * 100).toInt()}% Strength", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = pct,
                                modifier = Modifier.fillMaxWidth().clip(CircleShape).height(6.dp),
                                color = if (pct > 0.8f) Color(0xFF22C55E) else MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }

        // Saved Bookmarks List
        item {
            Text(
                "Saved Bookmarks & Favourites (${bookmarks.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (bookmarks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.2f))
                ) {
                    Text(
                        "No lessons or stories bookmarked yet! Click the bookmark star inside reader panels.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(bookmarks) { bmk ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(bmk.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Type: ${bmk.itemType.replace("_"," ")} • ${bmk.subtitle}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

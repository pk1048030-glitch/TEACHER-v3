package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TeacherCourse::class,
        TeacherLesson::class,
        StudentTopic::class,
        MoralStory::class,
        SavedQuiz::class,
        AiGeneratedItem::class,
        SavedBookmark::class,
        AppStat::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun academyDao(): AcademyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "teacher_base_academy_db"
                )
                    .addCallback(AcademyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AcademyDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDb(database.academyDao())
                }
            }
        }

        private suspend fun populateDb(dao: AcademyDao) {
            // --- POPULATE STATS ---
            dao.insertStat(AppStat("teacher_progress", 0))
            dao.insertStat(AppStat("student_progress", 0))
            dao.insertStat(AppStat("quizzes_taken", 0))
            dao.insertStat(AppStat("ai_generated_count", 0))

            // --- POPULATE TEACHER COURSES ---
            val courses = listOf(
                TeacherCourse(1, "Child Psychology (बाल मनोविज्ञान)", "Understand childhood cognitive stages, psychological development, behaviors, and standard theories of mental growth.", "Child Psychology"),
                TeacherCourse(2, "Classroom Management (कक्षा प्रबंधन)", "Learn strategies to maintain an active, high-participation environment styled for beginner tutors.", "Classroom Management"),
                TeacherCourse(3, "Lesson Planning (पाठ योजना)", "Craft highly constructive and interactive lesson outlines, aligning activities with learning outcomes.", "Lesson Planning"),
                TeacherCourse(4, "Confidence & Communication (आत्मविश्वास)", "Strategies for beginner tutors to build speaking confidence, manage parent relations, and speak clearly.", "Communication Skills"),
                TeacherCourse(5, "Teaching Skills (सिखाने के कौशल)", "Master active learning methods, questioning techniques, and the use of visuals to explain complex concepts.", "Teaching Skills")
            )
            dao.insertCourses(courses)

            // --- POPULATE TEACHER LESSONS ---
            val lessons = listOf(
                // Child Psychology
                TeacherLesson(
                    1, 1, "Understanding Learning Styles", "Child Psychology",
                    "Children have unique learning styles: Visual (seeing), Auditory (hearing), and Kinesthetic (doing). An effective primary teacher must blend all three.",
                    "To teach 'Water Cycle', show a colorful diagram (Visual), sing a fun rain song (Auditory), and make water vapor with warm water in a closed jar (Kinesthetic).",
                    "Do a 'Style Audit' of your class: observe 5 children and list which learning medium keeps them most engaged.",
                    "Tip: Don't label a kid as 'lazy'. They might just have an auditory style while you are using visual textbooks.",
                    "What are the three core learning styles in primary children?",
                    "Visual, Auditory, and Kinesthetic."
                ),
                TeacherLesson(
                    2, 1, "Positive Reinforcement Techniques", "Child Psychology",
                    "Praising effort rather than innate intelligence builds a growth mindset. Instead of 'You are so smart', say 'I can see how hard you tried!'",
                    "Rahul struggled with writing Hindi Swar. When he finished, say 'Excellent progress! Your loop on 'आ' is so neat today because of your practice!'",
                    "Implement a 'Star Pot' in your classroom. Put a small sticker inside it whenever a child displays effort or helpfulness.",
                    "Tip: Keep feedback direct, immediate, and specific to the work done.",
                    "Which built-in praise is more effective: 'You are a genius' or 'I am proud of your effort today'?",
                    "'I am proud of your effort' because it reinforces the work process rather than fixed traits."
                ),
                // Classroom Management
                TeacherLesson(
                    3, 2, "Establishing Routines & Warm-ups", "Classroom Management",
                    "Routines prevent chaos. Setting a standard 'Welcome routine' gets students focused and sets the tone immediately.",
                    "Start with standard 2-minute physical clap patterns. Clapping rhythmically forces kids to look up and replicate, capturing instant focus.",
                    "Design a 5-step morning routine (Bag in cubby, Notebook opened, Pencil sharpened, Silent reading, Morning greeting). Check daily.",
                    "Tip: Practice the routine explicitly as a sport rather than commands. Kids love rules that feel like a performance.",
                    "What is the ultimate purpose of classroom routines?",
                    "To automate standard transitions and save mental energy for academic learning."
                ),
                TeacherLesson(
                    4, 2, "The Traffic Light Technique", "Classroom Management",
                    "Visual boundaries are stronger than vocal shouting. Use Red, Yellow, and Green visual cards to communicate permitted noise levels.",
                    "Green card on the board means 'Group discussion noise' is allowed. Yellow means 'Whisper to buddy only'. Red means 'Pin drop silence'.",
                    "Draw a physical traffic light on cardboard. Point to the active color and have children replicate the appropriate voice level.",
                    "Tip: Never speak over a noisy classroom. Simply stand under the Red indicator, hold a silent countdown gesture, and wait.",
                    "Why are visual noise indicators more effective than vocal shouting?",
                    "Shouting increases the ambient room noise and model hostile expression, whereas visual tags remain calm and objective."
                ),
                // Lesson Planning
                TeacherLesson(
                    5, 3, "The 5E Instructional Model", "Lesson Planning",
                    "An effective lesson blueprint follows five stages: Engage, Explore, Explain, Elaborate, and Evaluate. This maximizes child memory.",
                    "Engage by hiding a magnet in your pocket and pulling iron clips. Explore by handing magnets to kids. Explain magnetic pull. Elaborate by finding magnetic materials. Evaluate with a fast quiz.",
                    "Construct a 5-minute 'Engage' hook for your next topic using a mystery box or a funny costume prop.",
                    "Tip: Active exploration must always precede formal lecturing." ,
                    "List the five stages of the 5E lesson planning model.",
                    "Engage, Explore, Explain, Elaborate, and Evaluate."
                )
            )
            dao.insertLessons(lessons)

            // --- POPULATE STUDENT TOPICS (NURSERY - CLASS 5) ---
            val studentTopics = listOf(
                // Nursery - Hindi
                StudentTopic(
                    1, "Nursery", "Hindi", "अक्षर ज्ञान: स्वर (Swar)", "Nursery Hindi",
                    "हिन्दी वर्णमाला में कुल 11 स्वर होते हैं। जैसे: अ, आ, इ, ई, उ, ऊ, ऋ, ए, ऐ, ओ, औ। आइए पहले तीन स्वरों को सीखें:\n\n1. अ से अनार (Anar) - सेहत के लिए बहुत अच्छा!\n2. आ से आम (Aam) - फलों का राजा!\n3. इ से इमली (Imli) - खट्टी-मीठी प्यारी!",
                    "[\"अ - अनार (Pomegranate)\", \"आ - आम (Mango)\", \"इ - इमली (Tamarind)\"]",
                    "[\"अ से क्या होता है? (अनार / बिल्ली)\", \"पलों का राजा आ से शुरू होता है? (हाँ / नहीं)\"]"
                ),
                // Nursery - Math
                StudentTopic(
                    2, "Nursery", "Math", "Introduction to Numbers 1 to 5", "Nursery Numbers",
                    "Let's learn to count objects using our fingers!\n\n1 One: ☝️ (One Sun)\n2 Two: ✌️ (Two Eyes)\n3 Three: 🤟 (Three Wheels on a Rickshaw)\n4 Four: ✋ minus 1 (Four legs of a Chair)\n5 Five: 🖐️ (Five fingers on one hand!)",
                    "[\"1 - One Apple\", \"2 - Two Stars\", \"3 - Three Birds\"]",
                    "[\"How many suns do we have in the sky? (1 / 2)\", \"Count: 🎈🎈. How many are there? (2 / 3)\"]"
                ),
                // UKG - English
                StudentTopic(
                    3, "UKG", "English", "CVC Words & Phonics", "Phonics",
                    "CVC words stand for Consonant-Vowel-Consonant. They are the base of reading! Let's sound them out:\n\n- a sound (/æ/): c-a-t (Cat), h-a-t (Hat), m-a-n (Man)\n- e sound (/e/): p-e-n (Pen), r-e-d (Red), h-e-n (Hen)\n- i sound (/ɪ/): p-i-n (Pin), b-i-g (Big), t-i-n (Tin)",
                    "[\"c-a-t -> CAT\", \"p-e-n -> PEN\", \"p-i-n -> PIN\"]",
                    "[\"Identify the rhyming word for CAT: (Hat / Pen)\", \"Choose the vowel in PEN: (e / p)\"]"
                ),
                // Class 1 - Math
                StudentTopic(
                    4, "Class 1", "Math", "Addition of Single Digits", "Basic Arithmetic",
                    "Addition means putting things together! Let's say you have 3 juicy oranges and your friend gives you 2 more oranges. How many do you have now?\n\n3 + 2 = 5! You count them all together: 1, 2, 3... 4, 5!",
                    "[\"2 + 2 = 4\", \"4 + 1 = 5\", \"5 + 3 = 8\"]",
                    "[\"What is 3 + 3? (6 / 5)\", \"If you have 4 pencils and buy 2 more, you have 6. (True / False)\"]"
                ),
                // Class 3 - EVS / Science
                StudentTopic(
                    5, "Class 3", "Science", "Living and Non-Living things", "EVS Foundation",
                    "Living things can breathe, grow, eat, move, and reproduce. Non-living things cannot do these things.\n\n- Living Things: Plants, Dogs, Humans, Birds, Butterflies.\n- Non-Living Things: Stone, Table, Phone, Car, Toy.",
                    "[\"Dog - Living\", \"Rock - Non-living\", \"Tree - Living\"]",
                    "[\"Does a plant breathe? (Yes / No)\", \"Which of these is non-living? (Toy Tiger / Real Tiger)\"]"
                ),
                // Class 5 - Reasoning
                StudentTopic(
                    6, "Class 5", "Reasoning", "Completing Number Series", "Mental Ability",
                    "Reasoning shows patterns. Look at this series: 2, 4, 6, 8, ...\nEach number increases by adding 2! So the next number is 8 + 2 = 10!\n\nLet's try: 5, 10, 15, 20, ...\nHere, each step increases by 5! The next number is 20 + 5 = 25!",
                    "[\"Series: 1, 3, 5, 7 -> Next is 9 (+2)\", \"Series: 10, 20, 30 -> Next is 40 (+10)\"]",
                    "[\"Find the next number: 3, 6, 9, 12, _ ? (15 / 16)\", \"In 2, 5, 8, 11, we add 3 at each step. (True / False)\"]"
                )
            )
            dao.insertStudentTopics(studentTopics)

            // --- POPULATE MORAL STORIES ---
            val stories = listOf(
                MoralStory(
                    1, "The Honest Woodcutter", "ईमानदार लकड़हारा",
                    "Once, an honest woodcutter accidentally dropped his axe into a deep river. He cried because he lost his livelihood. The River Goddess emerged and brought up a golden axe. 'Is this yours?' she asked. 'No,' he said. She brought a silver axe. 'No,' he said. Finally, she showed his iron axe. 'Yes! This is mine!' he smiled. Touched by his absolute honesty, the Goddess gifted him all three axes.",
                    "एक बार की बात है, एक ईमानदार लकड़हारा जंगल में नदी के पास लकड़ी काट रहा था। अचानक उसकी लोहे की कुल्हाड़ी नदी में गिर गई। वह दुखी होकर रोने लगा। उसकी ईमानदारी देखकर नदी की देवी जल से बाहर आईं। उन्होंने सोने की कुल्हाड़ी दिखाई और पूछा, 'क्या यह तुम्हारी है?' उसने कहा, 'नहीं'। देवी ने चांदी की दिखाई तो उसने फिर मना किया। अंत में देवी ने उसकी लोहे की कुल्हाड़ी दिखाई। लकड़हारा खुशी से चिल्लाया 'हाँ, यही मेरी है!' देवी उसकी ईमानदारी से अत्यंत प्रसन्न हुईं और तीनों कुल्हाड़ियाँ उसे उपहार में दे दीं।",
                    "Moral", "Honesty always rewards you in the end.", "ईमानदारी का फल हमेशा मीठा होता है।", 3
                ),
                MoralStory(
                    2, "The Thirsty Crow", "प्यासा कौआ",
                    "On a hot summer day, a thirsty crow searched for water. He found a pitcher inside a garden, but the water level was very low. The crow could not reach it. Thinking quickly, he gathered small pebbles and dropped them one by one into the pitcher. Gradually, the water level rose to the top. The happy crow drank his fill and flew away with joy.",
                    "एक तपती गर्मी के दिन, एक प्यासा कौआ पानी की तलाश में उड़ रहा था। उसे एक बगीचे में पानी का एक घड़ा मिला, लेकिन उसमें पानी बहुत कम था। कौवे की चोंच पानी तक नहीं पहुँच पा रही थी। विचार करने के बाद, उसने एक तरकीब निकाली। उसने आस-पास से छोटे-छोटे कंकड़ इकट्ठे किए और उन्हें एक-एक करके घड़े में डालना शुरू किया। नीचे पड़े कंकड़ों के कारण घड़े का पानी ऊपर आ गया। कौवे ने भरपेट पानी पिया और खुशी से उड़ गया।",
                    "Panchatantra", "Where there is a will, there is a way.", "जहाँ चाह, वहाँ राह।", 2
                ),
                MoralStory(
                    3, "The Lion and the Mouse", "शेर और चूहा",
                    "A lion spared a tiny mouse's life after it accidentally woke him from sleep. The mouse promised to help the king of the jungle one day. The lion laughed. Weeks later, the lion was caught in a hunter's strong rope net. Hearing his roars, the small mouse rushed and chewed the ropes with its sharp teeth, freeing the lion. The grateful lion thanked the mouse and they became friends.",
                    "एक शेर अपनी नींद खराब होने पर एक छोटे से चूहे को मारने ही वाला था, लेकिन चूहे की दया की भीख पर उसने उसे छोड़ दिया। चूहे ने वादा किया कि वह एक दिन शेर की मदद करेगा, जिस पर शेर बहुत हँसा। कुछ हफ़्तों बाद, शेर शिकारियों के बिछाए जाल में फंस गया। वह जोर-जोर से दहाड़ने लगा। चूहे ने दहाड़ सुनी, वह तुरंत वहाँ पहुँचा और अपने पैने दाँतों से जाल की रस्सियों को कुतर दिया। शेर आज़ाद हो गया! उसने चूहे को शुक्रिया कहा और दोनों पक्के दोस्त बन गए।",
                    "Moral", "No one is too small or weak to help another.", "कोई भी इतना छोटा या कमज़ोर नहीं होता कि वह किसी बड़े की मदद न कर सके।", 3
                )
            )
            dao.insertStories(stories)

            // --- POPULATE QUIZZES ---
            val quizzes = listOf(
                SavedQuiz(
                    1, "Child Psychology Level 1", "Teacher", "Psychology",
                    "[" +
                            "{\"question\":\"What learning style involves learning by doing?\",\"options\":[\"Auditory\",\"Visual\",\"Kinesthetic\",\"Abstract\"],\"answerIndex\":2}," +
                            "{\"question\":\"Praising effort instead of smartness creates a...\",\"options\":[\"Fixed Mindset\",\"Growth Mindset\",\"Egoistic Mindset\",\"None\"],\"answerIndex\":1}," +
                            "{\"question\":\"Specific feedback is better than general praise.\",\"options\":[\"True\",\"False\"],\"answerIndex\":0}" +
                            "]",
                    "Easy", false, 0, 3
                ),
                SavedQuiz(
                    2, "Nursery Word and Number Fun", "Nursery", "Math & English",
                    "[" +
                            "{\"question\":\"What starts with the letter 'अ'?\",\"options\":[\"आम\",\"अनार\",\"इमली\",\"मछली\"],\"answerIndex\":1}," +
                            "{\"question\":\"How many fingers are there on one hand?\",\"options\":[\"3\",\"4\",\"5\",\"6\"],\"answerIndex\":2}," +
                            "{\"question\":\"Complete: 1, 2, 3, _, 5\",\"options\":[\"4\",\"6\",\"0\",\"9\"],\"answerIndex\":0}" +
                            "]",
                    "Easy", false, 0, 3
                ),
                SavedQuiz(
                    3, "Class 1 Addition Practice", "Class 1", "Math",
                    "[" +
                            "{\"question\":\"What is 4 + 3?\",\"options\":[\"6\",\"7\",\"8\",\"9\"],\"answerIndex\":1}," +
                            "{\"question\":\"True or False: 2 + 5 equals 8.\",\"options\":[\"True\",\"False\"],\"answerIndex\":1}," +
                            "{\"question\":\"If you have 5 baloons and get 1 more, how many do you have?\",\"options\":[\"4\",\"6\",\"5\",\"7\"],\"answerIndex\":1}" +
                            "]",
                    "Easy", false, 0, 3
                )
            )
            dao.insertQuizzes(quizzes)
        }
    }
}

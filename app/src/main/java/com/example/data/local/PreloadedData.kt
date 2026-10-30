package com.example.data.local

import com.example.data.model.ExamTest
import com.example.data.model.Question

object PreloadedData {

  fun getDefaultTests(): List<ExamTest> = listOf(
    // 1. OSSSC Combined Mock
    ExamTest(
      id = "mock_osssc_01",
      title = "OSSSC Combined Mock Test 2026 (RI, ARI, Amin, PEO)",
      category = "OSSSC",
      subject = "Odisha GK & Odia Grammar",
      durationMinutes = 15,
      totalMarks = 5,
      description = "Targeted test for OSSSC Revenue Inspector (RI), Amin, and Junior Assistant. Includes Odisha History, Geography, and Byakarana.",
      questions = listOf(
        Question(
          id = 1,
          topic = "Odisha History & Heritage",
          question = "In which year was the separate province of Odisha formed on linguistic basis?",
          options = listOf("1 April 1936", "15 August 1947", "26 January 1950", "1 April 1912"),
          correctIndex = 0,
          explanation = "Odisha became a separate province on 1 April 1936 with Sir John Austen Hubback as its first Governor, celebrated annually across the state as Utkal Divas."
        ),
        Question(
          id = 2,
          topic = "Odisha Geography",
          question = "Which of the following is the highest mountain peak in the state of Odisha?",
          options = listOf("Mahendragiri", "Deomali", "Gandhamardan", "Malayagiri"),
          correctIndex = 1,
          explanation = "Deomali, situated in Koraput district with an elevation of approximately 1,672 meters, is the highest peak in Odisha."
        ),
        Question(
          id = 3,
          topic = "Odia Grammar (ଓଡ଼ିଆ ବ୍ୟାକରଣ)",
          question = "What is the correct opposite word (ବିପରୀତାର୍ଥବୋଧକ ଶବ୍ଦ) of 'ଅନୁରାଗ' (Anuraga)?",
          options = listOf("ବିରାଗ (Biraga)", "ପ୍ରେମ (Prema)", "କ୍ରୋଧ (Krodha)", "ଦ୍ୱେଷ (Dwesa)"),
          correctIndex = 0,
          explanation = "'ଅନୁରାଗ' ର ପ୍ରକୃତ ବିପରୀତାର୍ଥବୋଧକ ଶବ୍ଦ ହେଉଛି 'ବିରାଗ'। ପ୍ରେମ କିମ୍ବା କ୍ରୋଧ ଏହାର ବିପରୀତ ନୁହେଁ।"
        ),
        Question(
          id = 4,
          topic = "Odisha Governance & Current GK",
          question = "Which lake in Odisha was designated as the first Ramsar Wetland Site of International Importance in India (1981)?",
          options = listOf("Ansupa Lake", "Chilika Lake", "Tampara Lake", "Kanjia Lake"),
          correctIndex = 1,
          explanation = "Chilika Lake, Asia's largest brackish water lagoon, was designated as the very first Ramsar site of India in 1981 along with Keoladeo National Park."
        ),
        Question(
          id = 5,
          topic = "Odisha Rivers & Dams",
          question = "On which river is the Hirakud Dam, one of the longest earthen dams in the world, constructed?",
          options = listOf("Brahmani", "Baitarani", "Mahanadi", "Rushikulya"),
          correctIndex = 2,
          explanation = "Hirakud Dam was built across the Mahanadi River near Sambalpur in Odisha, inaugurated in 1957 by Prime Minister Jawaharlal Nehru."
        )
      )
    ),

    // 2. OSSC CGL Mock
    ExamTest(
      id = "mock_ossc_cgl_01",
      title = "OSSC CGL & Combined Technical Prelims Mock",
      category = "OSSC",
      subject = "General Studies & Constitution",
      durationMinutes = 15,
      totalMarks = 5,
      description = "Standard OSSC Combined Graduate Level paper with Indian Polity, Odisha Economy, and Reasoning.",
      questions = listOf(
        Question(
          id = 1,
          topic = "Indian Constitution & Polity",
          question = "Under which Article of the Indian Constitution is the Odisha State Finance Commission constituted?",
          options = listOf("Article 243-I", "Article 280", "Article 324", "Article 243-K"),
          correctIndex = 0,
          explanation = "Article 243-I of the Constitution mandates the constitution of a State Finance Commission by the Governor to review financial position of Panchayats."
        ),
        Question(
          id = 2,
          topic = "Odisha Culture & Literature",
          question = "Who is revered with the title 'Adikabi' of Odia literature for authoring the Odia Sarala Mahabharata?",
          options = listOf("Sarala Das", "Upendra Bhanja", "Fakir Mohan Senapati", "Radhanath Ray"),
          correctIndex = 0,
          explanation = "Sarala Das, who lived during the reign of Gajapati Kapilendra Deva in the 15th century, is honoured as the Adikabi of Odia literature."
        ),
        Question(
          id = 3,
          topic = "Mental Ability & Reasoning",
          question = "Find the next term in the alphanumeric series: OD-01, OD-03, OD-07, OD-13, OD-21, ___?",
          options = listOf("OD-29", "OD-31", "OD-33", "OD-35"),
          correctIndex = 1,
          explanation = "The differences between consecutive numbers increase by 2: +2, +4, +6, +8, +10. Thus, 21 + 10 = 31 (OD-31)."
        ),
        Question(
          id = 4,
          topic = "Odisha Economy & Resources",
          question = "Odisha is the leading producer of which mineral in India, accounting for over 50% of the country's total output?",
          options = listOf("Bauxite & Chromite", "Mica & Copper", "Gold & Silver", "Uranium & Lignite"),
          correctIndex = 0,
          explanation = "Odisha is India's largest producer of both Chromite (over 90%) and Bauxite (over 50%), centered around Sukinda and Panchpatmali."
        ),
        Question(
          id = 5,
          topic = "Odisha History & Freedom Movement",
          question = "Who led the historic Salt Satyagraha at Inchudi (Balasore) in Odisha in 1930?",
          options = listOf("Acharya Harihar Das", "Gopabandhu Das", "Harekrushna Mahatab", "Biju Patnaik"),
          correctIndex = 0,
          explanation = "Acharya Harihar Das led the Salt Satyagraha march from Swaraj Ashram in Cuttack to Inchudi in Balasore on 12 April 1930."
        )
      )
    ),

    // 3. OPSC ASO & Civil Services Mock
    ExamTest(
      id = "mock_opsc_aso_01",
      title = "OPSC ASO & Civil Services (OCS) General Awareness Mock",
      category = "OPSC",
      subject = "OPSC Exam Paper",
      durationMinutes = 15,
      totalMarks = 5,
      description = "High-yield OPSC Assistant Section Officer and Odisha Civil Services preparation exam.",
      questions = listOf(
        Question(
          id = 1,
          topic = "Ancient Odisha History",
          question = "The Hathigumpha inscription on Udayagiri hills near Bhubaneswar describes the military conquests of which legendary Kalinga ruler?",
          options = listOf("Emperor Kharavela", "King Ashoka", "Gajapati Kapilendra Deva", "Chodaganga Deva"),
          correctIndex = 0,
          explanation = "The Hathigumpha inscription written in Brahmi script records the year-by-year exploits of the Chedi dynasty King Kharavela."
        ),
        Question(
          id = 2,
          topic = "Odisha Freedom Struggle",
          question = "Which place in Bhadrak district of Odisha is famous as 'Rakta Tirtha' due to the massacre of peaceful freedom fighters in September 1942?",
          options = listOf("Eram", "Puri", "Satyabadi", "Barabati"),
          correctIndex = 0,
          explanation = "Eram in Bhadrak is known as the 'Rakta Tirtha' (Pilgrimage of Blood) where 29 freedom fighters were killed in police firing during the Quit India movement."
        ),
        Question(
          id = 3,
          topic = "Odia Grammar (ଶୁଦ୍ଧ ବନାନ)",
          question = "Identify the grammatically correct word (ଶୁଦ୍ଧ ଶବ୍ଦ) among the following:",
          options = listOf("ନିରପରାଧ (Niraparadha)", "ନିରପରାଧୀ (Niraparadhi)", "ନିରପରାଧି (Niraparadhi short)", "ନିରାପରାଧୀ (Niraparadhi long)"),
          correctIndex = 0,
          explanation = "ବ୍ୟାକରଣ ଅନୁଯାୟୀ 'ନିରପରାଧ' ହିଁ ଶୁଦ୍ଧ ଶବ୍ଦ; 'ନିରପରାଧୀ' ଏକ ଅଶୁଦ୍ଧ ପ୍ରୟୋଗ ଅଟେ।"
        ),
        Question(
          id = 4,
          topic = "Environmental & Wildlife Heritage",
          question = "Which national park in Kendrapara district of Odisha is world-famous as the largest breeding sanctuary for Olive Ridley Sea Turtles?",
          options = listOf("Gahirmatha / Bhitarkanika", "Similipal National Park", "Kuldiha Sanctuary", "Debrigarh Sanctuary"),
          correctIndex = 0,
          explanation = "Gahirmatha Marine Sanctuary adjacent to Bhitarkanika is the world's most critical nesting beach for endangered Olive Ridley sea turtles."
        ),
        Question(
          id = 5,
          topic = "Odisha Temple Architecture",
          question = "Which dynastic king constructed the world-famous Sun Temple at Konark in the 13th century?",
          options = listOf("King Narasimhadeva I", "Anantavarman Chodaganga Deva", "Yayati Keshari", "Kapilendra Deva"),
          correctIndex = 0,
          explanation = "The Sun Temple at Konark was built by Eastern Ganga dynasty monarch King Narasimhadeva I around 1250 CE."
        )
      )
    ),

    // 4. OUSSSC Mock
    ExamTest(
      id = "mock_ousssc_01",
      title = "OUSSSC State & University Administrative Cadre Mock",
      category = "OUSSSC",
      subject = "Administrative & General Aptitude",
      durationMinutes = 15,
      totalMarks = 4,
      description = "Recruitment examination for Junior Assistants and Administrative Executives across Odisha State Universities.",
      questions = listOf(
        Question(
          id = 1,
          topic = "Higher Education in Odisha",
          question = "Which is the oldest university in Odisha, established in 1943 at Cuttack and later relocated to Vani Vihar, Bhubaneswar?",
          options = listOf("Utkal University", "Sambalpur University", "Berhampur University", "Ravenshaw University"),
          correctIndex = 0,
          explanation = "Utkal University is the oldest university in Odisha, established in 1943 during the prime ministership of Maharaja Krushna Chandra Gajapati."
        ),
        Question(
          id = 2,
          topic = "General Knowledge",
          question = "Who wrote the official state song of Odisha, 'Bande Utkala Janani'?",
          options = listOf("Kantakabi Laxmikanta Mohapatra", "Radhanath Ray", "Gopabandhu Das", "Madhusudan Rao"),
          correctIndex = 0,
          explanation = "Kantakabi Laxmikanta Mohapatra penned 'Bande Utkala Janani' in 1912, adopted as the official state song in 2020."
        ),
        Question(
          id = 3,
          topic = "Computer Awareness for Cadre",
          question = "What does the keyboard shortcut 'Ctrl + S' perform in office productivity software?",
          options = listOf("Save Document", "Select All", "Search Text", "Spell Check"),
          correctIndex = 0,
          explanation = "'Ctrl + S' is the universal shortcut to save changes to the current file."
        ),
        Question(
          id = 4,
          topic = "Odia Administrative Terminology",
          question = "What is the Odia equivalent for the administrative designation 'Revenue Inspector'?",
          options = listOf("ରାଜସ୍ୱ ନିରୀକ୍ଷକ", "ଗ୍ରାମ ରକ୍ଷୀ", "କାର୍ଯ୍ୟନିର୍ବାହୀ ଅଧିକାରୀ", "କନିଷ୍ଠ ସହାୟକ"),
          correctIndex = 0,
          explanation = "Revenue Inspector is officially designated as 'ରାଜସ୍ୱ ନିରୀକ୍ଷକ' in the Revenue & Disaster Management department."
        )
      )
    ),

    // 5. Teaching Exam Master Mock
    ExamTest(
      id = "mock_teaching_01",
      title = "Odisha Teaching Exams Master Mock (OTET, OSSTET, RHT, JT, OAVS)",
      category = "TEACHING",
      subject = "Pedagogy & Odia Byakarana",
      durationMinutes = 15,
      totalMarks = 5,
      description = "Essential pedagogy, child development, and teaching methodology for Odisha school teacher selections.",
      questions = listOf(
        Question(
          id = 1,
          topic = "Child Development & Pedagogy",
          question = "According to Jean Piaget's cognitive development theory, in which stage does a child develop abstract and logical thinking?",
          options = listOf(
            "Formal Operational Stage (11+ years)",
            "Sensorimotor Stage (0-2 years)",
            "Pre-operational Stage (2-7 years)",
            "Concrete Operational Stage (7-11 years)"
          ),
          correctIndex = 0,
          explanation = "In the Formal Operational Stage (ages 11 and above), adolescents gain the ability to think abstractly and reason deductively."
        ),
        Question(
          id = 2,
          topic = "Educational Policy in Odisha",
          question = "Under the Odisha 5T high school transformation initiative, which key areas are focused on in government high schools?",
          options = listOf(
            "Smart Classrooms, e-Libraries, Science Labs, and Sports Facilities",
            "Only Foreign Language coaching",
            "Only Commercial Farming",
            "Private Tuition Centres"
          ),
          correctIndex = 0,
          explanation = "Odisha's 5T High School Transformation initiative modernizes government schools with smart interactive classrooms, STEM labs, e-libraries, and sports."
        ),
        Question(
          id = 3,
          topic = "Odia Pedagogy (ଭାଷା ଶିକ୍ଷାଦାନ)",
          question = "Which of the following is considered the fundamental primary skill in language acquisition (LSRW sequence)?",
          options = listOf(
            "ଶ୍ରବଣ (Listening)",
            "କଥନ (Speaking)",
            "ପଠନ (Reading)",
            "ଲିଖନ (Writing)"
          ),
          correctIndex = 0,
          explanation = "ଭାଷା ଶିକ୍ଷଣର ପ୍ରଥମ ଏବଂ ମୌଳିକ ସୋପାନ ହେଉଛି 'ଶ୍ରବଣ' (Listening - L in LSRW)।"
        ),
        Question(
          id = 4,
          topic = "Assessment & Evaluation",
          question = "Formative Assessment (ଗଠନମୂଳକ ମୂଲ୍ୟାୟନ) is primarily conducted for what purpose?",
          options = listOf(
            "Continuous monitoring and improvement during the teaching-learning process",
            "Assigning final letter grades at the end of the academic year only",
            "Punishing low-scoring students",
            "Calculating school financial audit"
          ),
          correctIndex = 0,
          explanation = "Formative evaluation provides ongoing feedback to teachers and students during instruction to correct learning gaps immediately."
        ),
        Question(
          id = 5,
          topic = "Inclusive Education",
          question = "The Right to Education (RTE) Act 2009 guarantees free and compulsory education to all children of which age group?",
          options = listOf("6 to 14 years", "3 to 6 years", "14 to 18 years", "5 to 10 years", "4 to 12 years"),
          correctIndex = 0,
          explanation = "Under the RTE Act 2009 (Article 21A), free and compulsory education is guaranteed for children aged 6 to 14 years."
        )
      )
    ),

    // 6. SBI Clerk Prelims Full Test 7 (CBT Mock Test matching exact reference screenshot)
    ExamTest(
      id = "mock_sbi_clerk_07",
      title = "SBI Clerk Prelims Full Test 7",
      category = "OSSC",
      subject = "English, Numerical & Reasoning Ability",
      durationMinutes = 60,
      totalMarks = 100,
      description = "Full 3-section simulation test matching TCS iON CBT pattern with English Language, Numerical Ability, and Reasoning Ability.",
      questions = listOf(
        // Section 1: English Language
        Question(
          id = 1,
          topic = "English Language",
          question = "Select the most appropriate synonym for the word 'METICULOUS':",
          options = listOf(
            "Extremely careful and precise in details",
            "Careless and disorganized in execution",
            "Slow and sluggish in routine tasks",
            "Aggressive and confrontation oriented",
            "Uncertain and hesitant about conclusions"
          ),
          correctIndex = 0,
          explanation = "'Meticulous' means showing great attention to detail; very careful and precise."
        ),
        Question(
          id = 2,
          topic = "English Language",
          question = "In the sentence below, identify the part containing an error: 'Neither the candidate (A) / nor the invigilators (B) / was present (C) / in the exam hall (D) / No error (E)':",
          options = listOf("A", "B", "C", "D", "E"),
          correctIndex = 2,
          explanation = "When subjects are joined by 'neither... nor', the verb agrees with the closer subject ('the invigilators' - plural), so it should be 'were present'."
        ),
        Question(
          id = 3,
          topic = "English Language",
          question = "Choose the correct idiom meaning 'to face a difficult situation with courage':",
          options = listOf(
            "Bite the bullet",
            "Beat around the bush",
            "Burn the midnight oil",
            "Spill the beans",
            "Bark up the wrong tree"
          ),
          correctIndex = 0,
          explanation = "'Bite the bullet' means to face a grim or difficult situation with courage and fortitude."
        ),

        // Section 2: Numerical Ability
        Question(
          id = 4,
          topic = "Numerical Ability",
          question = "If a sum of money doubles itself in 5 years at simple interest, in how many years will it become four times of itself at the same rate of interest?",
          options = listOf("10 years", "12 years", "15 years", "20 years", "25 years"),
          correctIndex = 2,
          explanation = "For doubling, SI = P in 5 yrs -> R = 20%. For becoming 4 times, SI = 3P -> Time = (3P * 100) / (P * 20) = 15 years."
        ),
        Question(
          id = 5,
          topic = "Numerical Ability",
          question = "A train 240 m long crosses a platform 360 m long in 30 seconds. What is the speed of the train in km/hr?",
          options = listOf("60 km/hr", "72 km/hr", "80 km/hr", "84 km/hr", "90 km/hr"),
          correctIndex = 1,
          explanation = "Total distance = 240 + 360 = 600 m. Speed = 600 / 30 = 20 m/s. In km/hr = 20 * (18/5) = 72 km/hr."
        ),
        Question(
          id = 6,
          topic = "Numerical Ability",
          question = "What should come in place of the question mark (?) in: 12, 26, 54, 110, 222, (?)",
          options = listOf("444", "446", "448", "452", "456"),
          correctIndex = 1,
          explanation = "Pattern is * 2 + 2: 12*2+2=26, 26*2+2=54, 54*2+2=110, 110*2+2=222, 222*2+2 = 446."
        ),

        // Section 3: Reasoning Ability
        Question(
          id = 7,
          topic = "Reasoning Ability",
          question = "In a certain code language, 'ODISHA' is coded as 'PEJTIB'. How will 'CUTTACK' be coded in that same language?",
          options = listOf("DVUUBDL", "DVVUBDL", "DUVUADL", "EWVVBDL", "DVUUCEL"),
          correctIndex = 0,
          explanation = "Each letter is shifted by +1: C->D, U->V, T->U, T->U, A->B, C->D, K->L = DVUUBDL."
        ),
        Question(
          id = 8,
          topic = "Reasoning Ability",
          question = "Pointing to a photograph, a woman says: 'He is the only son of the wife of my father.' How is the man related to the woman?",
          options = listOf("Father", "Brother", "Uncle", "Son", "Cousin"),
          correctIndex = 1,
          explanation = "Wife of my father = mother. Only son of mother = woman's brother (or herself if male, but here woman says he is the only son, so brother)."
        ),
        Question(
          id = 9,
          topic = "Reasoning Ability",
          question = "Statements: Some pens are books. All books are pencils. Conclusions: I. Some pencils are pens. II. All pens are pencils.",
          options = listOf(
            "Only conclusion I follows",
            "Only conclusion II follows",
            "Either I or II follows",
            "Neither I nor II follows",
            "Both I and II follow"
          ),
          correctIndex = 0,
          explanation = "Since Some pens are books and All books are pencils, the intersection of pens and pencils is non-empty. Only I follows."
        ),
        Question(
          id = 10,
          topic = "Reasoning Ability",
          question = "Five students P, Q, R, S, and T are sitting in a row facing North. R is sitting in the middle. P is to the immediate left of R. S is at the extreme right end. Who is to the immediate right of R?",
          options = listOf("P", "Q or T", "T", "Q", "Data inadequate"),
          correctIndex = 1,
          explanation = "Row order: _ P R _ S. R is in center (position 3). P is at 2. S is at 5. Position 4 can be occupied by either Q or T."
        )
      )
    )
  )

  val odishaDistricts = listOf(
    "Kalahandi",
    "Khordha (Bhubaneswar)",
    "Cuttack",
    "Ganjam (Berhampur)",
    "Sambalpur",
    "Balasore",
    "Mayurbhanj",
    "Sundargarh (Rourkela)",
    "Puri",
    "Bhadrak",
    "Angul",
    "Bargarh",
    "Bolangir",
    "Boudh",
    "Deogarh",
    "Dhenkanal",
    "Gajapati",
    "Jagatsinghpur",
    "Jajpur",
    "Jharsuguda",
    "Kandhamal",
    "Kendrapara",
    "Keonjhar",
    "Koraput",
    "Malkangiri",
    "Nabarangpur",
    "Nayagarh",
    "Nuapada",
    "Rayagada",
    "Subarnapur"
  )

  fun getDefaultCategories(): List<ExamCategoryEntity> = listOf(
    ExamCategoryEntity(
      id = "OPSC",
      title = "OPSC EXAMS",
      subtitle = "Mocks • Odisha History & Polity",
      badge = "CLASS 1 & 2 GAZETTED",
      subCategoryTag = "OCS / OAS • ASO • CIVIL SERVICES",
      iconEmoji = "🏛️",
      topics = "Odisha History, Polity, Geography, General Studies",
      displayOrder = 1,
      isActive = true,
      totalTestsCount = 1
    ),
    ExamCategoryEntity(
      id = "OSSC",
      title = "OSSC EXAMS",
      subtitle = "Mocks • Arithmetic & Reasoning",
      badge = "GROUP B & C CADRE",
      subCategoryTag = "CGL • SUB-INSPECTOR • JUNIOR CLERK",
      iconEmoji = "📋",
      topics = "General Studies, Reasoning, Economy, Indian Constitution",
      displayOrder = 2,
      isActive = true,
      totalTestsCount = 1
    ),
    ExamCategoryEntity(
      id = "OSSSC",
      title = "OSSSC EXAMS",
      subtitle = "Mocks • Odia Grammar & GK",
      badge = "DISTRICT REVENUE CADRE",
      subCategoryTag = "RI • ARI • AMIN • PEO • JA",
      iconEmoji = "🌾",
      topics = "Odisha GK, Odia Byakarana, Arithmetic, District Cadre",
      displayOrder = 3,
      isActive = true,
      totalTestsCount = 1
    ),
    ExamCategoryEntity(
      id = "OUSSSC",
      title = "OUSSSC EXAMS",
      subtitle = "Mocks • Junior Assistant & Staff",
      badge = "UNIVERSITY & STATE CADRE",
      subCategoryTag = "STATE & UNIVERSITY RECRUITMENT",
      iconEmoji = "🏛️",
      topics = "University Administrative Services, English, Computer Knowledge",
      displayOrder = 4,
      isActive = true,
      totalTestsCount = 1
    ),
    ExamCategoryEntity(
      id = "TEACHING",
      title = "TEACHING EXAM",
      subtitle = "Mocks • Pedagogy & Odia Byakarana",
      badge = "TEACHER RECRUITMENT",
      subCategoryTag = "OTET • OSSTET • RHT • JT • OAVS",
      iconEmoji = "🎓",
      topics = "Child Development, Pedagogy, Odia Literature, Evaluation",
      displayOrder = 5,
      isActive = true,
      totalTestsCount = 1
    )
  )
}

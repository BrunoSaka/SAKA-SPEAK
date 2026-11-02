package com.example.data.model

data class SpeechTip(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val steps: List<SpeechTipStep>,
    val summary: String
)

data class SpeechTipStep(
    val letter: String,
    val title: String,
    val description: String,
    val example: String
)

object SpeechFrameworks {
    val list = listOf(
        SpeechTip(
            id = "prep",
            title = "Metode PREP (Point, Reason, Example, Point)",
            subtitle = "Formula terbaik untuk bicara spontan & impromptu 2 menit",
            iconName = "Architecture",
            steps = listOf(
                SpeechTipStep(
                    letter = "P",
                    title = "Point (Pernyataan Utama)",
                    description = "Mulai langsung dengan kesimpulan atau posisi argumen Anda secara tegas dalam 10 detik pertama.",
                    example = "\"Menurut saya, AI tidak akan menggantikan manusia, melainkan menguatkan mereka yang adaptif.\""
                ),
                SpeechTipStep(
                    letter = "R",
                    title = "Reason (Alasan Kuat)",
                    description = "Jelaskan 'mengapa' posisi Anda valid. Berikan 1 atau 2 alasan mendasar.",
                    example = "\"Karena AI hanya alat pemroses data, sedangkan intuisi, empati, dan kreativitas moral tetap menjadi hak prerogatif manusia.\""
                ),
                SpeechTipStep(
                    letter = "E",
                    title = "Example (Contoh / Cerita)",
                    description = "Ilustrasikan dengan contoh nyata, data singkat, atau pengalaman personal yang mudah dibayangkan.",
                    example = "\"Sebagai contoh, seorang dokter yang menggunakan AI bisa mendiagnosis penyakit 3 kali lebih cepat, tetapi sentuhan empati dan pendampingan psikologis pasien hanya bisa dilakukan oleh dokter itu sendiri.\""
                ),
                SpeechTipStep(
                    letter = "P",
                    title = "Point (Penegasan Ulang)",
                    description = "Tutup dengan panggilan bertindak (call to action) atau penegasan ringkas yang berkesan.",
                    example = "\"Oleh karena itu, jangan takut pada AI; fokuslah mengasah kemanusiaan dan keunikan berpikir kita.\""
                )
            ),
            summary = "Gunakan metode PREP saat Anda hanya punya sedikit waktu untuk berpikir dan ingin jawaban yang terstruktur rapi."
        ),
        SpeechTip(
            id = "hook_story_offer",
            title = "Struktur Hook — Problem — Solusi",
            subtitle = "Menarik perhatian audiens dalam 15 detik pertama",
            iconName = "Bolt",
            steps = listOf(
                SpeechTipStep(
                    letter = "1",
                    title = "The Hook (Pancingan)",
                    description = "Buka dengan pertanyaan mengejutkan, fakta tak terduga, atau sebuah analogi visual.",
                    example = "\"Pernahkah Anda menyadari bahwa kita memeriksa ponsel lebih dari 80 kali sehari tanpa sadar?\""
                ),
                SpeechTipStep(
                    letter = "2",
                    title = "The Tension (Masalah & Dilema)",
                    description = "Gambarkan keresahan atau dampak buruk yang dirasakan jika masalah ini dibiarkan.",
                    example = "\"Ini bukan sekadar distraksi, melainkan pengikisan fokus mendalam yang membuat kita rentan cemas dan kehilangan arah.\""
                ),
                SpeechTipStep(
                    letter = "3",
                    title = "The Resolution (Tawaran Solusi)",
                    description = "Berikan jalan keluar yang masuk akal dan ajak audiens bertindak.",
                    example = "\"Kuncinya sederhana: luangkan waktu 30 menit detoks digital setiap pagi sebelum menyentuh notifikasi.\""
                )
            ),
            summary = "Sangat ampuh untuk presentasi persuasif dan pidato pembuka yang membutuhkan ketertarikan tinggi dari audiens."
        ),
        SpeechTip(
            id = "filler_words",
            title = "Trik Menghilangkan 'Eee...' & 'Hmm...'",
            subtitle = "Kekuatan jeda hening (The Power of Pause)",
            iconName = "VolumeMute",
            steps = listOf(
                SpeechTipStep(
                    letter = "A",
                    title = "Ganti Gumaman dengan Jeda Hening",
                    description = "Ketika otak Anda mencari kata berikutnya, tahan napas dan biarkan hening 1-2 detik. Audiens menganggap jeda sebagai tanda kebijaksanaan dan ketenangan.",
                    example = "Bukan: 'Menurut saya... eee... program ini... aaa... sangat bagus.'\nMelainkan: 'Menurut saya... [hening 1 detik] program ini memberikan dampak luar biasa.'"
                ),
                SpeechTipStep(
                    letter = "B",
                    title = "Tarik Napas Perut",
                    description = "Tarik napas lembut melalui hidung saat berpindah poin. Ini menenangkan detak jantung dan mengontrol kecepatan bicara.",
                    example = "Gunakan perpindahan slide atau paragraf sebagai sinyal bernapas rileks."
                ),
                SpeechTipStep(
                    letter = "C",
                    title = "Kunci Kalimat Pendek",
                    description = "Bicara dengan kalimat pendek (maksimal 10-15 kata). Kalimat panjang cenderung membingungkan diri sendiri dan mengundang filler words.",
                    example = "\"Buat titik lebih cepat. Mulai kalimat baru dengan subjek yang jelas.\""
                )
            ),
            summary = "Jeda hening adalah teman terbaik seorang pembicara hebat. Jangan takut pada kesunyian sesaat."
        ),
        SpeechTip(
            id = "vocal_variety",
            title = "Variasi Suara & Pacing",
            subtitle = "Agar pembicaraan tidak monoton dan membosankan",
            iconName = "GraphicEq",
            steps = listOf(
                SpeechTipStep(
                    letter = "1",
                    title = "Tempo (Kecepatan)",
                    description = "Percepat saat menceritakan aksi atau kegembiraan. Perlambat saat menyampaikan poin inti atau pesan filosofis.",
                    example = "Bicara cepat pada pengantar kejadian, lalu pelan dan tegas pada kesimpulan utama."
                ),
                SpeechTipStep(
                    letter = "2",
                    title = "Volume & Penekanan (Emphasis)",
                    description = "Tekankan kata kunci yang paling krusial dalam satu kalimat.",
                    example = "\"Ini bukan tentang KEMAMPUAN, ini tentang KEMAUAN.\""
                ),
                SpeechTipStep(
                    letter = "3",
                    title = "Artikulasi Mulut",
                    description = "Buka mulut lebih lebar saat berbicara agar vokal A-I-U-E-O terdengar jernih dan berenergi.",
                    example = "Latihan senam artikulasi wajah 1 menit sebelum mulai latihan."
                )
            ),
            summary = "Suara adalah instrumen musik Anda. Mainkan iramanya agar pendengar terhanyut dalam pesan Anda."
        )
    )
}

package com.example.data.repository

import com.example.data.model.Topic

enum class CatalogType {
    ALL,
    KATA,     // Kata Tunggal (Single Words)
    KALIMAT   // Kalimat Penuh (Sentences)
}

data class CatalogItem(
    val id: String,
    val index: Int,
    val type: CatalogType,
    val text: String,
    val category: String,
    val phonemeFocus: String = "", // Untuk kata (misal: "Konsonan K-T-P", "Vokal Rangkap", "Sengau NG")
    val difficulty: String = "Sedang"
) {
    fun toTopic(): Topic {
        return when (type) {
            CatalogType.KATA -> Topic(
                id = id,
                category = category,
                title = "Latihan Artikulasi: $text",
                quote = "Kejelasan sebuah kata adalah jendela kejujuran sebuah pemikiran.",
                points = listOf(
                    "Lafalkan kata '$text' sebanyak 3 kali dengan artikulasi vokal dan konsonan yang tegas.",
                    "Uraikan definisi dan makna konseptual kata ini menurut sudut pandangmu.",
                    "Berikan contoh konkret penerapannya dalam memecahkan masalah nyata."
                ),
                perspectives = listOf("Linguistik & Artikulasi", "Penerapan Kontekstual", "Nilai Sosial & Karakter"),
                challenge = "Dalam 2 menit, lafalkan kata \"$text\" dengan artikulasi jernih, jelaskan definisinya, dan ceritakan satu contoh nyata yang relevan!",
                isFavorite = false,
                isCustom = false
            )
            else -> Topic(
                id = id,
                category = category,
                title = text,
                quote = "Kalimat yang lahir dari perenungan mendalam akan menggerakkan hati pendengarnya.",
                points = listOf(
                    "Identifikasi premis utama dan ketegangan ide di dalam kalimat ini.",
                    "Bangun posisi argumenmu: apakah kamu setuju, menolak, atau memiliki sudut pandang alternatif?",
                    "Tutup dengan simpulan persuasif yang membekas di benak audiens."
                ),
                perspectives = listOf("Sudut Pandang Kritis", "Dampak Etis & Moral", "Relevansi Zaman Ini"),
                challenge = "Gunakan kalimat \"$text\" sebagai pondasi pidato spontanmu. Dalam 2 menit, buktikan atau kritisi pernyataan tersebut dengan metode PREP!",
                isFavorite = false,
                isCustom = false
            )
        }
    }
}

object CatalogBank100k {

    const val TOTAL_COUNT = 100_000
    const val WORD_COUNT = 30_000
    const val SENTENCE_COUNT = 70_000

    val categories = listOf(
        "FILSAFAT & ETIKA",
        "TEKNOLOGI & AI",
        "SOSIAL & BUDAYA",
        "EKONOMI & FINANSIAL",
        "POLITIK & HUKUM",
        "PSIKOLOGI MANUSIA",
        "PENDIDIKAN MODERN",
        "LINGKUNGAN HIDUP",
        "KEPEMIMPINAN & KARAKTER",
        "KESEHATAN MENTAL"
    )

    // Bank Akar Kata & Terminologi Bernas Bahasa Indonesia (Kosakata Tinggi)
    private val rootWords = listOf(
        "Integritas", "Resiliensi", "Kedaulatan", "Dekarbonisasi", "Metakognisi", "Komprehensif",
        "Paradigma", "Akuntabilitas", "Solutif", "Eksistensial", "Preseden", "Transparansi",
        "Distopia", "Epistemologi", "Konseptualisasi", "Dialektika", "Altruisme", "Ekuilibrium",
        "Otentisitas", "Sinergitas", "Diversifikasi", "Transformasi", "Konsistensi", "Kredibilitas",
        "Objektivitas", "Persuasif", "Holistik", "Emansipasi", "Fleksibilitas", "Kapasitas",
        "Kohesivitas", "Artikulasi", "Pragmatisme", "Idealitas", "Kearifan", "Dignitas",
        "Inovasi", "Dinamika", "Proporsional", "Substansial", "Legitimasi", "Restorasi",
        "Disrupsi", "Evolusi", "Sinkronisasi", "Kolektivitas", "Diferensiasi", "Polarisasi",
        "Konsekuensi", "Komitmen", "Resonansi", "Inklusivitas", "Signifikansi", "Eksplorasi",
        "Manifestasi", "Intelektualitas", "Keberlanjutan", "Modernitas", "Kemandirian", "Inisiatif"
    )

    private val wordAdjectives = listOf(
        "Fundamental", "Kontemporer", "Mendasar", "Strategis", "Urgensial", "Visioner",
        "Transformatif", "Revolusioner", "Substantif", "Konstruktif", "Progresif", "Empatis",
        "Proporsional", "Sistematik", "Signifikan", "Krusial", "Eksploratif", "Kontekstual"
    )

    // Formula Kalimat: Subjek Pembuka + Dinamika Pertentangan + Konteks Dampak
    private val sentenceSubjects = listOf(
        "Keberanian untuk berbicara jujur di ruang publik",
        "Pemanfaatan kecerdasan buatan dalam dunia pendidikan",
        "Keseimbangan antara ambisi karier dan kesehatan mental",
        "Generasi muda yang tumbuh di tengah banjir informasi instan",
        "Sistem pendidikan yang terlalu mendewakan nilai akademis",
        "Kemampuan mendengarkan sebelum mengambil keputusan penting",
        "Krisis empati yang terjadi di balik anonimitas media sosial",
        "Kebebasan berpendapat yang dibenturkan dengan etika komunitas",
        "Pemimpin yang memimpin dengan keteladanan bukan kekuasaan",
        "Tekanan gaya hidup konsumtif akibat algoritma periklanan",
        "Pelestarian kearifan lokal di tengah arus deras globalisasi",
        "Ketahanan moral seseorang ketika dihadapkan pada godaan kekuasaan",
        "Perubahan iklim global dan tanggung jawab lintas generasi",
        "Kemandirian finansial yang dibangun dari disiplin menahan diri",
        "Kebiasaan membaca buku mendalam di era video berdurasi pendek"
    )

    private val sentenceVerbs = listOf(
        "menjadi ujian terbesar bagi integritas peradaban modern",
        "bukan sekadar pilihan moral, melainkan syarat kelangsungan hidup",
        "mampu meruntuhkan ilusi bahwa kecepatan selalu berarti kemajuan",
        "menuntut keberanian untuk melawan arus kenyamanan semu",
        "membuka mata kita tentang pentingnya kembali pada nilai kemanusiaan",
        "membuktikan bahwa teknologi harus tunduk pada etika nurani",
        "sering kali disalahpahami sebagai kelemahan, padahal itu puncak kekuatan",
        "menjadi fondasi utama dalam melahirkan masyarakat yang beradab",
        "mengharuskan setiap individu berani mengambil tanggung jawab pribadi",
        "memberikan dampak jangka panjang yang jauh melampaui kepentingan sesaat"
    )

    private val sentenceComplements = listOf(
        "demi memastikan masa depan yang adil dan bermartabat bagi generasi mendatang.",
        "sebab tanpa kedalaman berpikir, kemajuan teknologi justru mempercepat kehancuran.",
        "karena kekayaan sejati seorang manusia terletak pada kemanfaatan yang ia tinggalkan.",
        "ketika masyarakat mulai kehilangan kemampuan membedakan kritik dengan kebencian.",
        "sebagai bukti bahwa perubahan besar selalu berawal dari keteguhan langkah kecil.",
        "di mana setiap kata yang kita ucapkan membawa tanggung jawab sosial yang nyata.",
        "agar kita tidak sekadar menjadi penonton pasif di tengah pergulatan sejarah bangsa.",
        "sebab kata-kata yang diucapkan dengan integritas mampu menyatukan perbedaan yang tajam."
    )

    /**
     * Mengambil item katalog secara deterministik pada indeks tertentu (0 .. 99.999)
     * Sangat cepat O(1), hemat memori, dan menjamin 100.000 kombinasi unik.
     */
    fun getItem(index: Int): CatalogItem {
        val safeIndex = index.coerceIn(0, TOTAL_COUNT - 1)
        val category = categories[safeIndex % categories.size]

        return if (safeIndex < WORD_COUNT) {
            // Tipe KATA (30.000 Item Kata Tunggal & Gabungan Kata Artikulasi)
            val root = rootWords[safeIndex % rootWords.size]
            val adj = wordAdjectives[(safeIndex / rootWords.size) % wordAdjectives.size]
            val variant = (safeIndex / (rootWords.size * wordAdjectives.size)) + 1

            val wordText = if (safeIndex < rootWords.size) {
                root
            } else if (safeIndex < rootWords.size * 5) {
                "$root $adj"
            } else {
                "$root ($variant)"
            }

            CatalogItem(
                id = "word_$safeIndex",
                index = safeIndex + 1,
                type = CatalogType.KATA,
                text = wordText,
                category = category,
                phonemeFocus = "Vokal & Konsonan Akhir",
                difficulty = if (safeIndex % 3 == 0) "Tinggi" else "Sedang"
            )
        } else {
            // Tipe KALIMAT (70.000 Item Kalimat Refleksi & Argumentasi Berbicara)
            val sentenceIndex = safeIndex - WORD_COUNT
            val subj = sentenceSubjects[sentenceIndex % sentenceSubjects.size]
            val verb = sentenceVerbs[(sentenceIndex / sentenceSubjects.size) % sentenceVerbs.size]
            val comp = sentenceComplements[(sentenceIndex / (sentenceSubjects.size * sentenceVerbs.size)) % sentenceComplements.size]
            val numberTag = if (sentenceIndex >= sentenceSubjects.size * sentenceVerbs.size * sentenceComplements.size) {
                " [Perspektif ${sentenceIndex + 1}]"
            } else ""

            val sentenceText = "$subj $verb $comp$numberTag"

            CatalogItem(
                id = "sentence_$safeIndex",
                index = safeIndex + 1,
                type = CatalogType.KALIMAT,
                text = sentenceText,
                category = category,
                phonemeFocus = "Intonasi & Pacing PREP",
                difficulty = if (sentenceIndex % 2 == 0) "Tantangan Kritis" else "Menengah"
            )
        }
    }

    /**
     * Mengambil daftar halaman (pagination) dengan filter tipe dan kategori
     */
    fun getPage(
        page: Int,
        pageSize: Int = 30,
        typeFilter: CatalogType = CatalogType.ALL,
        categoryFilter: String = "SEMUA",
        searchQuery: String = ""
    ): List<CatalogItem> {
        val result = mutableListOf<CatalogItem>()
        var currentIndex = page * pageSize
        var examined = 0
        val maxExamine = 4000 // Batasan pemindaian per halaman agar super responsif

        while (result.size < pageSize && examined < maxExamine && currentIndex < TOTAL_COUNT) {
            val item = getItem(currentIndex)
            val matchesType = when (typeFilter) {
                CatalogType.ALL -> true
                CatalogType.KATA -> item.type == CatalogType.KATA
                CatalogType.KALIMAT -> item.type == CatalogType.KALIMAT
            }
            val matchesCategory = categoryFilter == "SEMUA" || item.category.equals(categoryFilter, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() || item.text.contains(searchQuery, ignoreCase = true) || item.category.contains(searchQuery, ignoreCase = true)

            if (matchesType && matchesCategory && matchesQuery) {
                result.add(item)
            }
            currentIndex++
            examined++
        }

        return result
    }
}

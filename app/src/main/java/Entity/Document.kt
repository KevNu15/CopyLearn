package Entity

import java.time.LocalDate

class Document {
    private var id: String = ""
    private var title: String = ""
    private lateinit var captureDate: LocalDate
    private var imageUri: String = ""         // content://... or file://...
    private var recognizedText: String = ""   // OCR result text
    private var language: Language = Language()
    private var ocrConfidence: Double = 0.0   // 0.0..1.0

    constructor()

    constructor(
        id: String,
        title: String,
        captureDate: LocalDate,
        imageUri: String,
        recognizedText: String,
        language: Language,
        ocrConfidence: Double
    ) {
        this.id = id
        this.title = title
        this.captureDate = captureDate
        this.imageUri = imageUri
        this.recognizedText = recognizedText
        this.language = language
        this.ocrConfidence = ocrConfidence
    }

    var ID: String
        get() = this.id
        set(value) { this.id = value }

    var Title: String
        get() = this.title
        set(value) { this.title = value }

    var CaptureDate: LocalDate
        get() = this.captureDate
        set(value) { this.captureDate = value }

    var ImageUri: String
        get() = this.imageUri
        set(value) { this.imageUri = value }

    var RecognizedText: String
        get() = this.recognizedText
        set(value) { this.recognizedText = value }

    var Language: Language
        get() = this.language
        set(value) { this.language = value }

    var OcrConfidence: Double
        get() = this.ocrConfidence
        set(value) { this.ocrConfidence = value }
}

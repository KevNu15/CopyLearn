package Entity

class Language {
    private var code: String = ""   // e.g., "en", "es"
    private var name: String = ""   // e.g., "English", "Spanish"

    constructor()

    constructor(code: String, name: String) {
        this.code = code
        this.name = name
    }

    var Code: String
        get() = this.code
        set(value) { this.code = value }

    var Name: String
        get() = this.name
        set(value) { this.name = value }
}

package dev.zawarudo.holo.modules.urbandictionary;

public record UrbanDictionaryEntry(String term, String definition, String example, String link) {
    public boolean hasValidDefinition() {
        return definition != null && !definition.isBlank();
    }

    public boolean hasValidExample() {
        return example != null && !example.isBlank();
    }
}
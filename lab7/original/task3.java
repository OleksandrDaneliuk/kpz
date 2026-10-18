package variant2_documentprocessing;

// Опис предметної області: Електронний документообіг підприємства.
// Завдання коду: Редагування, збереження та друк документів різних форматів у єдиному робочому просторі.
//

public class Variant2_DocumentProcessing {

    public static class Document {
        private String title;
        private String content;
        private String filePath;

        public Document(String title, String content, String filePath) {
            this.title = title;
            this.content = content;
            this.filePath = filePath;
        }

        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getFilePath() { return filePath; }

        public void setContent(String newContent) {
            this.content = newContent;
        }

        public void save() {
            System.out.println("Saving document '" + title + "' to path: " + filePath);
        }

        public void print() {
            System.out.println("Printing document '" + title + "': " + content);
        }
    }

    public static class ReadOnlyPdfDocument extends Document {
        public ReadOnlyPdfDocument(String title, String content, String filePath) {
            super(title, content, filePath);
        }

        @Override
        public void setContent(String newContent) {
            // Порушення LSP: Заборона модифікації вмісту
            throw new UnsupportedOperationException("Cannot modify content of a read-only PDF document.");
        }

        @Override
        public void save() {
            // Порушення LSP: Відмова від виконання операції збереження
            throw new IllegalStateException("Read-only PDF documents cannot be saved/overwritten.");
        }
    }

    public static class DocumentEditor {
        public void appendWatermarkAndSave(Document doc, String watermark) {
            // Клієнтський код впевнений, що будь-який Document можна редагувати та зберігати
            doc.setContent(doc.getContent() + "
                    [Watermark: " + watermark + "]");
            doc.save();
        }
    }
}
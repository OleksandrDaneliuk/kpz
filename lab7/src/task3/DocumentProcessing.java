package task3;

public class DocumentProcessing {

    // ===== Immutable Value Objects with Guard Clauses (fail-fast) =====

    public record DocumentTitle(String value) {
        public DocumentTitle {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Document title must not be blank.");
            }
        }
    }

    public record FilePath(String value) {
        public FilePath {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("File path must not be blank.");
            }
        }
    }

    public record DocumentContent(String value) {
        public DocumentContent {
            if (value == null) {
                throw new IllegalArgumentException("Document content must not be null.");
            }
        }
    }

    // ===== Segregated contracts (capabilities instead of one fat base type) =====

    /** Contract: any document can be read and printed. No mutation is promised. */
    public interface ReadableDocument {
        String getTitle();
        String getContent();
        String getFilePath();

        default void print() {
            System.out.println("Printing document '" + getTitle() + "': " + getContent());
        }
    }

    /**
     * Contract: content can be replaced and persisted.
     * Postconditions: after setContent(x) getContent() equals x; after save() the state is persisted.
     */
    public interface EditableDocument extends ReadableDocument {
        void setContent(String newContent);
        void save();
    }

    // ===== Implementations =====

    public static class TextDocument implements EditableDocument {
        private final DocumentTitle title;
        private final FilePath filePath;
        private DocumentContent content;

        public TextDocument(String title, String content, String filePath) {
            this.title = new DocumentTitle(title);
            this.content = new DocumentContent(content);
            this.filePath = new FilePath(filePath);
        }

        @Override public String getTitle() { return title.value(); }
        @Override public String getContent() { return content.value(); }
        @Override public String getFilePath() { return filePath.value(); }

        @Override
        public void setContent(String newContent) {
            this.content = new DocumentContent(newContent); // guard: null is rejected immediately
        }

        @Override
        public void save() {
            System.out.println("Saving document '" + title.value() + "' to path: " + filePath.value());
        }
    }

    /** Fully immutable: it is NOT an EditableDocument, so clients can never try to modify or save it. */
    public static final class ReadOnlyPdfDocument implements ReadableDocument {
        private final DocumentTitle title;
        private final DocumentContent content;
        private final FilePath filePath;

        public ReadOnlyPdfDocument(String title, String content, String filePath) {
            this.title = new DocumentTitle(title);
            this.content = new DocumentContent(content);
            this.filePath = new FilePath(filePath);
            if (!this.filePath.value().toLowerCase().endsWith(".pdf")) {
                throw new IllegalArgumentException("PDF document must have a .pdf file path.");
            }
        }

        @Override public String getTitle() { return title.value(); }
        @Override public String getContent() { return content.value(); }
        @Override public String getFilePath() { return filePath.value(); }
    }

    // ===== Client code: requires exactly the capability it uses =====

    public static class DocumentEditor {
        public void appendWatermarkAndSave(EditableDocument doc, String watermark) {
            if (doc == null) {
                throw new IllegalArgumentException("Document must not be null.");
            }
            if (watermark == null || watermark.isBlank()) {
                throw new IllegalArgumentException("Watermark must not be blank.");
            }
            doc.setContent(doc.getContent() + "\n[Watermark: " + watermark + "]");
            doc.save();
        }
    }

    // ===== Demo =====

    public static void main(String[] args) {
        DocumentEditor editor = new DocumentEditor();

        TextDocument report = new TextDocument("Report", "Quarterly results.", "/docs/report.txt");
        editor.appendWatermarkAndSave(report, "CONFIDENTIAL");
        report.print();

        ReadOnlyPdfDocument contract = new ReadOnlyPdfDocument("Contract", "Terms...", "/docs/contract.pdf");
        contract.print();
        // editor.appendWatermarkAndSave(contract, "X"); // does not compile: PDF is not an EditableDocument

        try {
            new TextDocument(" ", "text", "/docs/a.txt"); // fail-fast: invalid invariant
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}

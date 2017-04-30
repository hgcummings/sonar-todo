package io.hgc.sonar.todo.plugin.testHelper;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultIndexedFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.batch.fs.internal.Metadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Consumer;

public class InputFileBuilder {
    private static final char PADDING_CHAR = '#';
    private static final int DEFAULT_PADDING_AMOUNT = 10;

    private String text;
    private int lineNumber = DEFAULT_PADDING_AMOUNT;
    private int offset = DEFAULT_PADDING_AMOUNT;

    public static InputFileBuilder createInputFile() {
        return new InputFileBuilder();
    }

    public InputFileBuilder containingText(String text) {
        this.text = text;
        return this;
    }

    public InputFileBuilder atLine(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public InputFileBuilder atOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public InputFile build() throws IOException {
        File file = File.createTempFile(UUID.randomUUID().toString(), ".src");
        file.deleteOnExit();

        FileWriter writer = new FileWriter(file);
        writeContents(writer);
        writer.close();

        DefaultIndexedFile indexedFile = new DefaultIndexedFile(
                "moduleKey", Paths.get(file.getParentFile().getPath()), file.getName());

        Charset charset = Charset.defaultCharset();
        Metadata metadata = new FileMetadata().readMetadata(file, charset);
        return new DefaultInputFile(indexedFile, new DummyMetadataGenerator(charset, metadata));
    }

    private void writeContents(FileWriter writer) throws IOException {
        addRowPadding(writer, lineNumber);
        addColumnPadding(writer, offset);
        writer.write(text);
        addRowPadding(writer, DEFAULT_PADDING_AMOUNT);
        addColumnPadding(writer, DEFAULT_PADDING_AMOUNT);
    }

    /**
     * Adds extra padding lines.
     *
     * Rows are one-indexed, as per {@link org.sonar.api.batch.fs.TextPointer}
     */
    private void addRowPadding(FileWriter writer, int amount) throws IOException {
        for (int row = 1; row < amount; ++row) {
            writer.write(PADDING_CHAR);
            writer.write('\n');
        }
    }

    /**
     * Adds extra padding characters to a line.
     *
     * Columns are one-indexed, as per {@link org.sonar.api.batch.fs.TextPointer}
     */
    private void addColumnPadding(FileWriter writer, int amount) throws IOException {
        for (int col = 0; col < amount; ++col) {
            writer.write(PADDING_CHAR);
        }
    }

    private static class DummyMetadataGenerator implements Consumer<DefaultInputFile> {
        private final Charset charset;
        private final Metadata metadata;

        DummyMetadataGenerator(Charset charset, Metadata metadata) {
            this.charset = charset;
            this.metadata = metadata;
        }

        @Override
        public void accept(DefaultInputFile defaultInputFile) {
            defaultInputFile.setCharset(charset);
            defaultInputFile.setMetadata(metadata);
        }
    }
}

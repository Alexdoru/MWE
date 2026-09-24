package fr.alexdoru.mwe.utils;

import com.google.gson.reflect.TypeToken;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

public class JsonUtilTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    static class Pojo {

        String name;
        int value;
        String nullableField;

        Pojo() {}

        Pojo(String name, int value, String nullableField) {
            this.name = name;
            this.value = value;
            this.nullableField = nullableField;
        }

    }

    // ---------------------------------------------------------------
    // readFromFile
    // ---------------------------------------------------------------

    @Test
    public void readFromFile_validJson_returnsDeserializedObject() throws IOException {
        final File file = tempFolder.newFile("valid.json");
        Files.write(file.toPath(), "{\"name\":\"abc\",\"value\":42}".getBytes(StandardCharsets.UTF_8));

        final Pojo result = JsonUtil.readFromFile(file, Pojo.class);

        assertNotNull(result);
        assertEquals("abc", result.name);
        assertEquals(42, result.value);
    }

    @Test
    public void readFromFile_withParameterizedType_returnsDeserializedList() throws IOException {
        final File file = tempFolder.newFile("list.json");
        Files.write(file.toPath(),
                "[{\"name\":\"a\",\"value\":1},{\"name\":\"b\",\"value\":2}]".getBytes(StandardCharsets.UTF_8));
        final Type listType = new TypeToken<List<Pojo>>() {}.getType();

        final List<Pojo> result = JsonUtil.readFromFile(file, listType);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("a", result.get(0).name);
        assertEquals("b", result.get(1).name);
    }

    @Test
    public void readFromFile_emptyFile_returnsNullWithoutThrowing() throws IOException {
        final File file = tempFolder.newFile("empty.json");

        final Pojo result = JsonUtil.readFromFile(file, Pojo.class);

        assertNull(result);
    }

    @Test
    public void readFromFile_nonExistentFile_returnsNull() {
        final File file = new File(tempFolder.getRoot(), "does-not-exist.json");

        final Pojo result = JsonUtil.readFromFile(file, Pojo.class);

        assertNull(result);
    }

    @Test
    public void readFromFile_directoryInsteadOfFile_returnsNull() throws IOException {
        final File dir = tempFolder.newFolder("iAmADirectory");

        final Pojo result = JsonUtil.readFromFile(dir, Pojo.class);

        assertNull(result);
    }

    @Test
    public void readFromFile_malformedJsonSyntax_returnsNullAndDoesNotUseFallback() throws IOException {
        final File file = tempFolder.newFile("malformed-syntax.json");
        Files.write(file.toPath(), "{ this is not json broken".getBytes(StandardCharsets.UTF_8));

        final Pojo result = JsonUtil.readFromFile(file, Pojo.class);

        assertNull(result);
    }

    @Test
    public void readFromFile_malformedUtf8Encoding_fallsBackAndReadsSuccessfully() throws IOException {
        final File file = tempFolder.newFile("bad-encoding.json");
        // Valid JSON *structure* but with an invalid UTF-8 byte sequence embedded
        // inside the "name" string value.
        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            os.write("{\"name\":\"".getBytes(StandardCharsets.UTF_8));
            os.write(new byte[]{(byte) 0x80, (byte) 0xAF}); // invalid UTF-8 continuation bytes
            os.write("\",\"value\":7}".getBytes(StandardCharsets.UTF_8));
        }

        final Pojo result = JsonUtil.readFromFile(file, Pojo.class);

        // Strict UTF-8 decoding fails -> fallback (lenient FileReader) kicks in
        // and succeeds, replacing the bad bytes with U+FFFD instead of failing.
        assertNotNull(result);
        assertEquals(7, result.value);
        assertTrue(result.name.indexOf('\uFFFD') >= 0);
    }

    // ---------------------------------------------------------------
    // writeJsonToFile
    // ---------------------------------------------------------------

    @Test
    public void writeJsonToFile_validObject_writesFileAndReturnsTrue() {
        final File file = new File(tempFolder.getRoot(), "output.json");
        final Pojo pojo = new Pojo("hello", 99, null);

        final boolean result = JsonUtil.writeJsonToFile(file, pojo);

        assertTrue(result);
        assertTrue(file.exists());
    }

    @Test
    public void writeJsonToFile_writesPrettyPrintedJson() throws IOException {
        final File file = new File(tempFolder.getRoot(), "pretty.json");

        assertTrue(JsonUtil.writeJsonToFile(file, new Pojo("hello", 99, null)));

        final String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        assertTrue("expected pretty-printed (multi-line) JSON", content.contains("\n"));
    }

    @Test
    public void writeJsonToFile_serializesNullFields() throws IOException {
        final File file = new File(tempFolder.getRoot(), "nulls.json");

        assertTrue(JsonUtil.writeJsonToFile(file, new Pojo("hello", 99, null)));

        final String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        // GSON_PRETTY uses serializeNulls(), unlike plain Gson which omits nulls.
        assertTrue(content.contains("\"nullableField\": null"));
    }

    @Test
    public void writeJsonToFile_writtenContentRoundTrips() {
        final File file = new File(tempFolder.getRoot(), "roundtrip.json");
        final Pojo original = new Pojo("hello", 99, "not-null");

        assertTrue(JsonUtil.writeJsonToFile(file, original));
        final Pojo readBack = JsonUtil.readFromFile(file, Pojo.class);

        assertNotNull(readBack);
        assertEquals(original.name, readBack.name);
        assertEquals(original.value, readBack.value);
        assertEquals(original.nullableField, readBack.nullableField);
    }

    @Test
    public void writeJsonToFile_overwritesExistingFileContent() {
        final File file = new File(tempFolder.getRoot(), "overwrite.json");
        assertTrue(JsonUtil.writeJsonToFile(file, new Pojo("first", 1, null)));

        assertTrue(JsonUtil.writeJsonToFile(file, new Pojo("second", 2, null)));

        final Pojo readBack = JsonUtil.readFromFile(file, Pojo.class);
        assertNotNull(readBack);
        assertEquals("second", readBack.name);
        assertEquals(2, readBack.value);
    }

    @Test
    public void writeJsonToFile_leavesNoTemporaryFileBehindOnSuccess() {
        final File file = new File(tempFolder.getRoot(), "no-tmp-leftover.json");

        assertTrue(JsonUtil.writeJsonToFile(file, new Pojo("hello", 1, null)));

        assertFalse(new File(tempFolder.getRoot(), "no-tmp-leftover.json.tmp").exists());
    }

    @Test
    public void writeJsonToFile_createsMissingParentDirectories() {
        final File file = new File(tempFolder.getRoot(), "nested/sub/dir/output.json");

        final boolean result = JsonUtil.writeJsonToFile(file, new Pojo("hello", 1, null));

        assertTrue(result);
        assertTrue(file.exists());
    }

    @Test
    public void writeJsonToFile_parentPathBlockedByRegularFile_returnsFalseWithoutThrowing() throws IOException {
        // Create a plain file where a directory is expected, so
        // Files.createDirectories(parent) fails with FileAlreadyExistsException.
        final File blocker = tempFolder.newFile("blocker");
        final File target = new File(blocker, "output.json");

        final boolean result = JsonUtil.writeJsonToFile(target, new Pojo("hello", 1, null));

        assertFalse(result);
    }

    @Test
    public void writeJsonToFile_tmpPathBlockedByDirectory_returnsFalseAndLeavesTargetUntouched() throws IOException {
        final File target = new File(tempFolder.getRoot(), "blocked-write.json");
        // Pre-create "<file>.tmp" as a directory so Files.newBufferedWriter(tmp) fails.
        Files.createDirectories(new File(tempFolder.getRoot(), "blocked-write.json.tmp").toPath());

        final boolean result = JsonUtil.writeJsonToFile(target, new Pojo("hello", 1, null));

        assertFalse(result);
        assertFalse("target file should not have been created on write failure", target.exists());
    }

    @Test
    public void writeJsonToFile_nullObject_writesJsonNullAndReturnsTrue() throws IOException {
        final File file = new File(tempFolder.getRoot(), "null-obj.json");

        final boolean result = JsonUtil.writeJsonToFile(file, null);

        assertTrue(result);
        final String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8).trim();
        assertEquals("null", content);
    }

}
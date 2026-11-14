package tubes.prak.pbo.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

public class ImageStorage {

    private static final Path ROOT = Paths.get("upload");
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png");
    private static final long MAX_SIZE = 2L * 1024 * 1024; // 2 MB

    /** Menyalin file ke upload/<subFolder>/ dan mengembalikan relative path-nya. null jika source null. */
    public static String save(File source, String subFolder) throws IOException {
        if (source == null) return null;

        if (!source.isFile()) {
            throw new IOException("File gambar tidak ditemukan!");
        }
        if (source.length() > MAX_SIZE) {
            throw new IOException("Ukuran gambar maksimal 2 MB!");
        }

        String ext = getExtension(source.getName());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new IOException("Format gambar harus JPG atau PNG!");
        }
        // pastikan isinya benar-benar gambar, bukan file lain yang diganti ekstensinya
        if (ImageIO.read(source) == null) {
            throw new IOException("File bukan gambar yang valid!");
        }

        Path dir = ROOT.resolve(subFolder);
        Files.createDirectories(dir);

        String fileName = UUID.randomUUID() + "." + ext;
        Path target = dir.resolve(fileName);
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

        // pakai "/" agar konsisten di semua OS
        return target.toString().replace('\\', '/');
    }

    /** Hapus file berdasarkan relative path (dipakai untuk rollback bila insert DB gagal). */
    public static void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Files.deleteIfExists(Paths.get(relativePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase();
    }
}
package com.bupt.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Stores CV files under WEB-INF/uploads/cv and streams them safely.
 */
public final class CvStorage {

    private static final String CV_DIR = "/WEB-INF/uploads/cv";

    private CvStorage() {}

    public static boolean isSafeCvFileName(String name) {
        if (name == null || name.isEmpty() || name.contains("..") || name.contains("/") || name.contains("\\")) {
            return false;
        }
        String lower = name.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx");
    }

    /**
     * Writes the stored CV file to the response, or sets an HTTP error.
     */
    public static void streamCv(ServletContext ctx, HttpServletResponse resp, String stored)
            throws IOException {
        if (stored == null || stored.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!isSafeCvFileName(stored)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String base = ctx.getRealPath(CV_DIR);
        if (base == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        Path basePath = Paths.get(base).toAbsolutePath().normalize();
        Path file = basePath.resolve(stored).normalize();
        if (!file.startsWith(basePath) || !Files.isRegularFile(file)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String mime = Files.probeContentType(file);
        if (mime == null) {
            mime = "application/octet-stream";
        }
        resp.setContentType(mime);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + stored + "\"");
        Files.copy(file, resp.getOutputStream());
    }
}

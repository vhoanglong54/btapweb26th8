package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.UserProfile;
import vn.iotstar.model.User;
import vn.iotstar.service.ProfileService;
import vn.iotstar.service.impl.ProfileServiceImpl;
import vn.iotstar.util.Constant;

/** Authenticated users can update only their own display information and avatar. */
@WebServlet("/profile")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class ProfileController extends HttpServlet {
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private final ProfileService profiles = new ProfileServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User account = account(request);
        if (account == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
        show(request, response, profiles.findById(account.getId()), null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User account = account(request);
        if (account == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
        UserProfile existing = profiles.findById(account.getId());
        if (existing == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

        String newAvatar = null;
        try {
            Part image = request.getPart("image");
            if (image != null && image.getSize() > 0) newAvatar = saveAvatar(image);
            profiles.update(account.getId(), request.getParameter("fullName"), request.getParameter("phone"), newAvatar);
            account.setFullName(request.getParameter("fullName").trim());
            account.setPhone(normalize(request.getParameter("phone")));
            if (newAvatar != null) account.setAvatar(newAvatar);
            request.getSession().setAttribute(Constant.SESSION_ACCOUNT, account);
            deleteOldAvatar(existing.getAvatar(), newAvatar);
            response.sendRedirect(request.getContextPath() + "/profile?updated=1");
        } catch (IllegalArgumentException exception) {
            deleteNewAvatar(newAvatar);
            existing.setFullName(normalize(request.getParameter("fullName")));
            existing.setPhone(normalize(request.getParameter("phone")));
            show(request, response, existing, exception.getMessage());
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response, UserProfile profile, String alert) throws ServletException, IOException {
        if (profile == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        request.setAttribute("profile", profile);
        request.setAttribute("alert", alert);
        // SiteMesh buffers this page. Including the JSP preserves that buffer on Tomcat 11.
        request.getRequestDispatcher("/views/profile.jsp").include(request, response);
    }

    private String saveAvatar(Part part) throws IOException {
        String originalName = part.getSubmittedFileName() == null ? "" : new File(part.getSubmittedFileName()).getName();
        int dot = originalName.lastIndexOf('.');
        if (dot < 1) throw new IllegalArgumentException("Ảnh phải có phần mở rộng hợp lệ.");
        String extension = originalName.substring(dot + 1).toLowerCase();
        if (!extension.matches("png|jpe?g|gif|webp") || !IMAGE_TYPES.contains(part.getContentType())) {
            throw new IllegalArgumentException("Chỉ nhận ảnh PNG, JPG, GIF hoặc WEBP.");
        }
        String name = UUID.randomUUID() + "." + extension;
        File target = new File(Constant.profileDirectory(), name);
        part.write(target.getAbsolutePath());
        return "profile/" + name;
    }

    private void deleteOldAvatar(String oldAvatar, String newAvatar) {
        if (oldAvatar == null || newAvatar == null || !oldAvatar.startsWith("profile/")) return;
        File oldFile = new File(Constant.DIR, oldAvatar);
        if (oldFile.isFile()) oldFile.delete();
    }

    private void deleteNewAvatar(String avatar) {
        if (avatar == null) return;
        File file = new File(Constant.DIR, avatar);
        if (file.isFile()) file.delete();
    }

    private User account(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object account = session == null ? null : session.getAttribute(Constant.SESSION_ACCOUNT);
        return account instanceof User user ? user : null;
    }

    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}

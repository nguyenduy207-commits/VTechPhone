package vtechphone.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.InputStream;
import java.util.Map;

public class CloudinaryUtil {

    // 🔥 Thông tin Cloudinary của bạn
    private static final String CLOUD_NAME = "w0zi5msw";
    private static final String API_KEY = "128386219218261";
    private static final String API_SECRET = "YU5juyIwCzMUgQIYJdFQcUbb5TU";

    private static Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", CLOUD_NAME,
        "api_key", API_KEY,
        "api_secret", API_SECRET,
        "secure", true
    ));

    /**
     * Upload file ảnh lên Cloudinary
     */
    public static String uploadFile(InputStream fileInputStream, String publicId) {
        try {
            byte[] fileBytes = fileInputStream.readAllBytes();

            Map uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.asMap(
                "public_id", publicId,
                "folder", "vtechphone/products" 
            ));
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi upload ảnh lên Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Xóa ảnh trên Cloudinary
     * @param publicId Tên ảnh cần xóa (chỉ cần số ID, ví dụ: 5)
     */
    public static void deleteFile(String publicId) {
        try {
            // 🔥 Quan trọng: Khi gọi xóa, phải cộng thêm tên folder mà lúc upload đã dùng
            String fullPublicId = "vtechphone/products/" + publicId;
            
            cloudinary.uploader().destroy(fullPublicId, ObjectUtils.emptyMap());
            System.out.println("✅ Đã xóa ảnh thành công trên Cloudinary: " + fullPublicId);
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu muốn báo lỗi ra giao diện Admin, bạn có thể ném exception ra tại đây
            // throw new RuntimeException("Lỗi xóa ảnh Cloudinary: " + e.getMessage());
        }
    }
}
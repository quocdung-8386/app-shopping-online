package com.example.apponline.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.apponline.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "order_status_channel";
    private static final String CHANNEL_NAME = "Thông báo Trạng thái Đơn hàng";

    /**
     * Tạo kênh thông báo (chỉ cần thiết cho Android 8.0 Oreo trở lên)
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo về trạng thái đặt hàng và giao hàng.");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Gửi thông báo cục bộ sau khi đặt hàng thành công
     */
    public static void showOrderSuccessNotification(Context context, String orderId, double totalAmount) {

        // 1. Tạo kênh thông báo trước khi gửi (đảm bảo gọi hàm này trong Application hoặc MainActivity)
        createNotificationChannel(context);

        String title = "🎉 Đặt hàng thành công!";
        String content = "Đơn hàng #" + orderId + " trị giá " + String.format("%,.0f VNĐ", totalAmount) + " đã được xác nhận.";

        // 2. Tạo đối tượng Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Thay bằng icon thông báo của bạn
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content)); // Hiển thị nội dung đầy đủ

        // 3. Gửi thông báo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            // Sử dụng orderId làm ID thông báo để tránh thông báo bị ghi đè
            int notificationId = orderId.hashCode();
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
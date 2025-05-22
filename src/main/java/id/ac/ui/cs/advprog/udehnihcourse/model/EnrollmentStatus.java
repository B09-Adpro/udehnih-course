package id.ac.ui.cs.advprog.udehnihcourse.model;

public enum EnrollmentStatus {
    ENROLLED, // Pembayaran sudah berhasil diverifikasi Staff
    PENDING, // Default value, masih onprogress payment
    DROPPED, // Student unenroll course yang sempat di-enroll-nya
    PAYMENT_FAILED // Pembayaran gagal
}

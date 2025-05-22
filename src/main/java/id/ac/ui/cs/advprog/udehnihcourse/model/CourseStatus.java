package id.ac.ui.cs.advprog.udehnihcourse.model;

public enum CourseStatus {
    DRAFT, // Tutor sudah buat course, tapi belum lengkap persyaratan minimal harus ada satu article dan section
    PENDING_REVIEW, // Course sudah siap dan siap untuk di-review Staff
    REJECTED, // Staff menemukan masalah dan course perlu direvisi
    PUBLISHED, // Course sudah live dan bisa didaftari Student
}

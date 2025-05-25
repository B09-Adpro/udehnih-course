package id.ac.ui.cs.advprog.udehnihcourse.command.Enrollment;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.PaymentCallbackDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProcessPaymentCallbackCommand implements EnrollmentCommand<Void> {
    private final CourseEnrollmentService enrollmentService;
    private final PaymentCallbackDTO callback;

    @Override
    public Void execute() {
        enrollmentService.processPaymentCallback(callback);
        return null;
    }
}

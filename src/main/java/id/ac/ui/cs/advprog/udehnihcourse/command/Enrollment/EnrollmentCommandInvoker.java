package id.ac.ui.cs.advprog.udehnihcourse.command.Enrollment;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentCommandInvoker {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentCommandInvoker.class);

    public <T> T executeCommand(EnrollmentCommand<T> command) {
        String commandName = command.getClass().getSimpleName();
        logger.info("Starting execution of command: {}", commandName);

        try {
            if (command instanceof EnrollStudentCommand) {
                EnrollStudentCommand enrollCommand = (EnrollStudentCommand) command;
                logger.info("Processing enrollment - Student: {}, Course: {}, Payment: {}",
                        enrollCommand.getStudentId(), enrollCommand.getCourseId(), enrollCommand.getPaymentMethod());
            } else if (command instanceof ProcessPaymentCallbackCommand) {
                ProcessPaymentCallbackCommand paymentCommand = (ProcessPaymentCallbackCommand) command;
                logger.info("Processing payment callback for enrollment ID: {}",
                        paymentCommand.getCallback().getEnrollmentId());
            } else if (command instanceof GetEnrollmentsCommand) {
                GetEnrollmentsCommand getCommand = (GetEnrollmentsCommand) command;
                logger.info("Retrieving enrollments for student: {}", getCommand.getStudentId());
            }

            long startTime = System.currentTimeMillis();
            T result = command.execute();
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Command {} completed in {}ms", commandName, duration);
            return result;
        } catch (Exception e) {
            logger.error("Error executing {}: {}", commandName, e.getMessage(), e);
            throw e;
        }
    }
}
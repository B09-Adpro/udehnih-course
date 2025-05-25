package id.ac.ui.cs.advprog.udehnihcourse.command.Enrollment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentCommandInvokerTest {

    @Mock
    private EnrollmentCommand<String> mockCommand;

    private EnrollmentCommandInvoker invoker;

    @BeforeEach
    void setUp() {
        invoker = new EnrollmentCommandInvoker();
    }

    @Test
    void whenExecuteCommand_thenReturnResult() {
        // Arrange
        when(mockCommand.execute()).thenReturn("success");

        // Act
        String result = invoker.executeCommand(mockCommand);

        // Assert
        assertEquals("success", result);
        verify(mockCommand, times(1)).execute();
    }

    @Test
    void whenCommandThrowsException_thenPropagateException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Test exception");
        when(mockCommand.execute()).thenThrow(exception);

        // Act & Assert
        Exception thrown = assertThrows(RuntimeException.class, () -> {
            invoker.executeCommand(mockCommand);
        });

        assertEquals("Test exception", thrown.getMessage());
        verify(mockCommand, times(1)).execute();
    }
}
package nl.merapar.stack.service.rest.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * A single constraint violation.
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConstraintViolation   {

    /**
     * The field that causes the violation.
     *
     * Example: post.creationdate
     */
    private String field;

    /**
     * The violation message.
     *
     * Example: may not be empty
     */
    @NotNull
    private String message;
}

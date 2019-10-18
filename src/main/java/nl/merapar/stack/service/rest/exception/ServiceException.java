package nl.merapar.stack.service.rest.exception;

import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Exception to throw if a backend error occurs. When using REST this will
 * result in a status 500 (Internal Server Error) with a traceable problem object.
 *
 * The traceable elements that you can use are 'code' and 'uuid' to specify an
 * application error resp. unique id.
 *
 */
@Data
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(callSuper=false)
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = -5912875202353399577L;

	private Integer code;

	@Setter
	private UUID uuid;

	public ServiceException(String message) {
		this(0, message, null);
	}
	
	public ServiceException(Throwable t) {
		this(0, ExceptionUtils.getRootCauseMessage(t), t);
	}

	public ServiceException(String message, Throwable t) {
		this(0, message, t);
	}

	public ServiceException(int code, String message) {
		this(code, message, null);
	}

	public ServiceException(int code, Throwable t) {
		this(code, ExceptionUtils.getRootCauseMessage(t), t);
	}

	public ServiceException(int code, String message, Throwable t) {
		this(UUID.randomUUID(), code, message, t);
	}

    public ServiceException(UUID uuid, int code, String message) {
	    this(uuid, code, message, null);
    }

	public ServiceException(UUID uuid, int code, String message, Throwable t) {
		super(message, t);
		this.uuid = uuid;
		this.code = code;
	}

	public String getCodeString() {
	    return code != null ? code.toString() : null;
    }

}


package com.userregistration.error;

import java.util.List;

public record ErrorResponse(List<ApiFieldError> errors) {
}

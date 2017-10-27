package com.unb.tracker.validator;

import com.unb.tracker.model.User;
import com.unb.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "Email field cannot be empty.");
        if (!isValidEmailAddress(user.getEmail()))
            errors.rejectValue("email", "Invalid email address.");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Username field cannot be empty.");
        if (user.getUsername().length() <= 6 || user.getUsername().length() >= 32)
            errors.rejectValue("username", "Username must be between 6 and 32 characters.");

        if (userService.findByUsername(user.getUsername()) != null)
            errors.rejectValue("username", "Someone already has that username.");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Password field cannot be empty.");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32)
            errors.rejectValue("password", "Password must be between 8 and 32 characters in length.");

        if (!user.getPasswordConfirm().equals(user.getPassword()))
            errors.rejectValue("passwordConfirm", "Your passwords don't match.");
    }

    /* Brandon: Reused a robust email validator from a personal project */
    public boolean isValidEmailAddress(String emailAddress) {
        //---Preliminary Check---//
        if (emailAddress == null)
            return false;
        if (emailAddress.length() == 0)
            return false;
        if (emailAddress.length() > 320)
            return false;
        if (!emailAddress.contains("@"))
            return false;
        if (!emailAddress.matches("\\A\\p{ASCII}*\\z"))
            return false;

        String addressWithQuotedMarkers = emailAddress.replaceAll("\\\".*\\\"", "[QUOTED]");
        String addressWithoutQuotedMarkers = emailAddress.replaceAll("\\\".*\\\"", "");
        int removedChars = emailAddress.length() - addressWithoutQuotedMarkers.length();

        //---Ensure Only One Unquoted @ Symbol---//
        if (!addressWithoutQuotedMarkers.contains("@"))
            return false;
        if (addressWithoutQuotedMarkers.length() - addressWithoutQuotedMarkers.replace("@", "").length() > 1)
            return false;

        //---Ensure Only Valid Characters---//
        String[] invalidChars = {",", ";", "<", ">", " "};
        for (String character : invalidChars) {
            if (addressWithoutQuotedMarkers.contains(character))
                return false;
        }

        String localPartWithQuotedMarkers = addressWithQuotedMarkers.substring(0, addressWithQuotedMarkers.indexOf('@'));
        String localPartWithoutQuotedMarkers = addressWithoutQuotedMarkers.substring(0, addressWithoutQuotedMarkers.indexOf('@'));
        String domainPartWithQuotedMarkers = addressWithQuotedMarkers.substring(addressWithQuotedMarkers.indexOf('@') + 1);

        //---Ensure Local Text Quoted Text Left Anchored, Commented Text Left/Right Anchored, No Leading/Trailing Dot---//
        //---Ensure Domain Text No Quoted Text, No Double Dot, No Leading Dash---//
        String[] patterns = {".\\[QUOTED\\]", ".\\(.*\\).", "^\\..|.\\.$", "\\[QUOTED\\]", "\\.\\.", "^-|-$"};
        for (String pattern : patterns) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(localPartWithQuotedMarkers);
            if (m.find())
                return false;
        }

        //---Ensure Valid Character Lengths---//
        if (localPartWithoutQuotedMarkers.length() + removedChars > 64)
            return false;
        if (localPartWithoutQuotedMarkers.length() == 0 && removedChars == 0)
            return false;
        if (domainPartWithQuotedMarkers.length() > 255)
            return false;

        //---Ensure Each DNS Label Has Valid Length---//
        String[] DNSLabel = domainPartWithQuotedMarkers.split("\\.");
        for (String label : DNSLabel) {
            if (label.length() >= 64)
                return false;
        }

        return true;
    }
}

package org.edx.mobile.module.registration.view;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import org.edx.mobile.R;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.module.registration.model.RegistrationFormField;

class RegistrationEditTextView implements IRegistrationFieldView {

    protected static final Logger logger = new Logger(RegistrationEditTextView.class);
    protected RegistrationFormField mField;
    private View mView;
    protected TextInputLayout mTextInputLayout;
    protected TextInputEditText mTextInputEditText;
    protected TextView mErrorTextView;

    public RegistrationEditTextView(RegistrationFormField field, View view) {
        // create and configure view and save it to an instance variable
        this.mField = field;
        this.mView = view;

        mTextInputLayout = (TextInputLayout) view.findViewById(R.id.register_edit_text_til);
        mTextInputEditText = (TextInputEditText) view.findViewById(R.id.register_edit_text_tilEt);
        mErrorTextView = (TextView) view.findViewById(R.id.input_error);

        // set max lines for this input to be 1
        mTextInputEditText.setLines(1);

        // apply max length
        if (mField.getRestriction().getMaxLength() > 0) {
            // otherwise, you may end up disabling the field

            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(mField.getRestriction().getMaxLength());
            mTextInputEditText.setFilters(FilterArray);
        }

        // set hint
        mTextInputLayout.setHint(mField.getLabel());

        // set default
        mTextInputEditText.setText(mField.getDefaultValue());

        // hide error text view
        mErrorTextView.setVisibility(View.GONE);

        // This tag is necessary for End-to-End tests to work properly
        mTextInputLayout.setTag(mField.getName());
    }

    public boolean setRawValue(String value){
        mTextInputEditText.setText(value);
        return true;
    }

    @Override
    public JsonElement getCurrentValue() {
        // turn text view content into a JsonElement and return it
        return new JsonPrimitive(mTextInputEditText.getText().toString());
    }

    @Override
    public boolean hasValue() {
        return !mTextInputEditText.getText().toString().isEmpty();
    }

    @Override
    public RegistrationFormField getField() {
        return mField;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void handleError(String error, boolean requestAccessibility) {
        if (!TextUtils.isEmpty(error)) {
            Spanned result = Html.fromHtml(error);
            mErrorTextView.setText(result);
            mErrorTextView.setVisibility(View.VISIBLE);
            if (requestAccessibility) {
                mErrorTextView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        }
        else {
            logger.warn("error message not provided, so not informing the user about this error");
        }
    }

    @Override
    public boolean isValidInput(boolean requestAccessibilityOnInvalid) {
        // hide error as we are re-validating the input
        mErrorTextView.setVisibility(View.GONE);

        // check if this is required field and has an input value
        if (mField.isRequired() && !hasValue()) {
            String errorMessage = mField.getErrorMessage().getRequired();
            if(errorMessage==null || errorMessage.isEmpty()){
                errorMessage = getView().getResources().getString(R.string.error_enter_field,
                        mField.getLabel());
            }
            handleError(errorMessage, requestAccessibilityOnInvalid);
            return false;
        }

        // check if length restrictions are followed
        int inputLength = getCurrentValue().getAsString().length();
        if (inputLength < mField.getRestriction().getMinLength()) {
            String errorMessage = mField.getErrorMessage().getMinLength();
            if(errorMessage==null || errorMessage.isEmpty()){
                errorMessage = getView().getResources().getString(R.string.error_min_length,
                        mField.getLabel(), mField.getRestriction().getMinLength());
            }
            handleError(errorMessage, requestAccessibilityOnInvalid);
            return false;
        }
        if (mField.getRestriction().getMaxLength() > 0
                && inputLength > mField.getRestriction().getMaxLength()) {
            String errorMessage = mField.getErrorMessage().getMaxLength();
            if(errorMessage==null || errorMessage.isEmpty()){
                errorMessage = getView().getResources().getString(R.string.error_max_length,
                        mField.getLabel(), mField.getRestriction().getMaxLength());
            }
            handleError(errorMessage, requestAccessibilityOnInvalid);
            return false;
        }

        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        mTextInputLayout.setEnabled(enabled);
    }

    @Override
    public void setActionListener(IActionListener actionListener) {
        // no actions for this field
    }
}

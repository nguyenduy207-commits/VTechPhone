package vtechphone.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("OTPUtil - sinh mã OTP")
class OTPUtilTest {

    @ParameterizedTest(name = "OTP độ dài {0}")
    @ValueSource(ints = {1, 4, 6, 10})
    @DisplayName("OTP có đúng độ dài yêu cầu và chỉ gồm chữ số")
    void otpHasRequestedLengthAndOnlyDigits(int length) {
        String otp = OTPUtil.generateOTP(length);

        assertEquals(length, otp.length());
        assertTrue(otp.matches("\\d+"), "OTP chỉ được chứa chữ số nhưng nhận: " + otp);
    }

    @Test
    @DisplayName("Độ dài 0 trả về chuỗi rỗng (biên dưới)")
    void zeroLength_returnsEmptyString() {
        assertEquals("", OTPUtil.generateOTP(0));
    }

    @Test
    @DisplayName("Sinh 200 mã liên tiếp: mã nào cũng hợp lệ 6 chữ số")
    void manyOtps_areAllValid() {
        for (int i = 0; i < 200; i++) {
            assertTrue(OTPUtil.generateOTP(6).matches("\\d{6}"));
        }
    }
}

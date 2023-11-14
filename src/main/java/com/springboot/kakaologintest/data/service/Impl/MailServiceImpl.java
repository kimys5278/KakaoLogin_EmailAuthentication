package com.springboot.kakaologintest.data.service.Impl;

import com.springboot.kakaologintest.data.entity.EmailConfirmation;
import com.springboot.kakaologintest.data.repository.ConfirmationRepository;
import com.springboot.kakaologintest.data.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender emailSender;
    private final ConfirmationRepository confirmationRepository;

    @Override
    public String sendSimpleMessage(String to) throws Exception {
        String ePw = createKey(); // 인증번호 생성

        // 이메일과 인증번호를 데이터베이스에 저장
        EmailConfirmation confirmation = new EmailConfirmation();
        confirmation.setPersonalEmail(to);
        confirmation.setConfirmationCode(ePw);
        confirmationRepository.save(confirmation);

        MimeMessage message = createMessage(to, ePw); // 수정된 createMessage 메서드 호출
        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw;
    }

    // 인증번호 검증 메서드
    public boolean verifyEmail(String confirmationCode) {
        EmailConfirmation confirmation = confirmationRepository.findByConfirmationCode(confirmationCode);
        if (confirmation != null && !confirmation.isVerified()) {
            confirmation.setVerified(true);
            confirmationRepository.save(confirmation);
            return true;
        }
        return false;
    }

    private MimeMessage createMessage(String to, String ePw) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("이메일 인증");
        String msgg = "";
        // 메일 내용 구성
        msgg += "<div style='margin:20px;'>";
        msgg += "<h1> 밥뭇냐 </h1>";
        msgg += "<br>";
        msgg += "<p>인증번호 입니다.</p>";
        msgg += "<br>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += ePw + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("nankys0510@naver.com", "DidYouEatLunch"));//보내는 사람

        return message;
    }

    public static String createKey() {
        int number = (int) (Math.random() * 90000) + 100000;
        return String.valueOf(number);
    }
}

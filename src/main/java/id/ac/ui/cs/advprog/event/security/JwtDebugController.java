//package id.ac.ui.cs.advprog.event.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/debug")
//public class JwtDebugController {
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//
//    @GetMapping("/token")
//    public String debugToken(@RequestParam String token) {
//        // Decode token without verification first
//        jwtTokenProvider.decodeTokenWithoutVerification(token);
//
//        // Try to validate
//        boolean isValid = jwtTokenProvider.validateToken(token);
//
//        if (isValid) {
//            String userId = jwtTokenProvider.getUserIdFromJWT(token);
//            String role = jwtTokenProvider.getRoleFromJWT(token);
//            return "Token valid! UserId: " + userId + ", Role: " + role;
//        } else {
//            return "Token validation failed";
//        }
//    }
//}
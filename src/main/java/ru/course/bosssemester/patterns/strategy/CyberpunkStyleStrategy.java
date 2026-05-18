package ru.course.bosssemester.patterns.strategy;
import org.springframework.stereotype.Component;
@Component public class CyberpunkStyleStrategy implements StyleStrategy { public String styleName(){return "CYBERPUNK";} public String promptPart(){return "киберпанк, неон, техно-демон из дедлайнов, футуристический город, драматический свет";} }

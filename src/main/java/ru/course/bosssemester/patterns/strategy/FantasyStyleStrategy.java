package ru.course.bosssemester.patterns.strategy;
import org.springframework.stereotype.Component;
@Component public class FantasyStyleStrategy implements StyleStrategy { public String styleName(){return "FANTASY";} public String promptPart(){return "эпическое фэнтези, магический финальный босс, детальная броня, мрачное подземелье";} }

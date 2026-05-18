package ru.course.bosssemester.patterns.strategy;
import org.springframework.stereotype.Component;
@Component public class ComicStyleStrategy implements StyleStrategy { public String styleName(){return "COMIC";} public String promptPart(){return "комикс, выразительные контуры, динамичная поза, яркая плакатная композиция";} }

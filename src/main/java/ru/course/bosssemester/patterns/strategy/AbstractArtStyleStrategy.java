package ru.course.bosssemester.patterns.strategy;
import org.springframework.stereotype.Component;
@Component public class AbstractArtStyleStrategy implements StyleStrategy { public String styleName(){return "ABSTRACT_ART";} public String promptPart(){return "абстрактный арт, символическая сущность семестра, геометрия, эмоции через форму и цвет";} }

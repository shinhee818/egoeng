package sh.egoeng.api.quiz.service.wordquiz;

import lombok.RequiredArgsConstructor;
import sh.egoeng.domain.word.Category;
import sh.egoeng.domain.word.CategoryRepository;
import sh.egoeng.domain.word.CategoryWordRepository;
import sh.egoeng.domain.word.Word;
import sh.egoeng.domain.word.WordLevel;
import sh.egoeng.domain.word.WordRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 단어 쿼리 빌더
 * 카테고리와 레벨 필터링을 체이닝 방식으로 처리
 */
@RequiredArgsConstructor
public class WordQueryBuilder {
    private final WordRepository wordRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryWordRepository categoryWordRepository;

    private List<Word> words;
    private WordLevel level;

    /**
     * 카테고리로 필터링 시작
     */
    public WordQueryBuilder byCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            this.words = wordRepository.findAll();
            return this;
        }

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryName));

        List<Long> wordIds = categoryWordRepository.findByCategoryId(category.getId())
                .stream()
                .map(cw -> cw.getWord().getId())
                .collect(Collectors.toList());

        if (wordIds.isEmpty()) {
            throw new IllegalArgumentException("No words found in category: " + categoryName);
        }

        this.words = wordRepository.findAllById(wordIds);
        return this;
    }

    /**
     * 레벨로 필터링
     */
    public WordQueryBuilder byLevel(String levelString) {
        if (levelString != null && !levelString.trim().isEmpty()) {
            this.level = WordLevel.valueOf(levelString.toUpperCase());
        }
        return this;
    }

    /**
     * 필터링된 단어 목록 반환
     */
    public List<Word> build() {
        if (words == null) {
            words = wordRepository.findAll();
        }

        if (level != null) {
            words = words.stream()
                    .filter(word -> word.getLevel() == level)
                    .collect(Collectors.toList());
        }

        return words;
    }
}














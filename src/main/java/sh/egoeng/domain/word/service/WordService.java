package sh.egoeng.domain.word.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.domain.word.Word;
import sh.egoeng.domain.word.WordRepository;
import sh.egoeng.domain.word.WordSearchRepository;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class WordService {
    private final WordRepository wordRepository;
    private final WordSearchRepository wordSearchRepository;

    @Transactional(readOnly = true)
    public Optional<Word> findById(Long wordId) {
        return wordRepository.findById(wordId);
    }

    @Transactional(readOnly = true)
    public Page<WordSearchDtoProjection> searchWordsByText(String searchText, PageRequest pageRequest) {
        return wordSearchRepository.findWordsByQuery(searchText, pageRequest);
    }
}

package sh.egoeng.domain.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 카테고리 이름으로 조회
     * @param name 카테고리 이름
     * @return Category (없으면 Optional.empty())
     */
    Optional<Category> findByName(String name);

    /**
     * 카테고리 이름 존재 여부 확인
     * @param name 카테고리 이름
     * @return 존재 여부
     */
    boolean existsByName(String name);
}














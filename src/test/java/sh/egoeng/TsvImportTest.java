package sh.egoeng;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.init.TsvImportService;

@SpringBootTest
class TsvImportTest {

    @Autowired
    TsvImportService tsvImportService;

    @Test
    @Commit
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    void importMulti() throws Exception {
        tsvImportService.importAllMultiThread();
    }
}
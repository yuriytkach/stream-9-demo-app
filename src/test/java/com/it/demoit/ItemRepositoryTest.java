package com.it.demoit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;

@DBRider
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DBUnit(
  caseSensitiveTableNames = true
)
class ItemRepositoryTest extends AbstractDBTest {

  @Autowired
  ItemRepository itemRepository;

  @ParameterizedTest
  @DataSet(value = { "ItemRepositoryTest/items.xml" })
  @CsvSource({
    "hello, 1",
    "abc, 2",
    "xyz, "
  })
  void shouldFindByName(final String name, final Long expectedId) {
    final ItemEntity entity = itemRepository.findByName(name);
    if (expectedId == null) {
      assertThat(entity).isNull();
    } else {
      assertThat(entity).isNotNull();
      assertThat(entity.getId()).isEqualTo(expectedId);
      assertThat(entity.getName()).isEqualTo(name);
      assertThat(entity.getValue()).isNull();
    }
  }

  @Test
  @ExpectedDataSet(value = {
    "ItemRepositoryTest/items-after-insert.yml"
  })
  void shouldSaveEntity() {
    final ItemEntity entity = new ItemEntity();
    entity.setName("New Name");
    //entity.setValue(42);
    final ItemEntity savedEntity = itemRepository.save(entity);
    assertThat(savedEntity.getId()).isNotNull();
  }

  @Test
  @DataSet("ItemRepositoryTest/items.yml")
  @ExpectedDataSet("ItemRepositoryTest/items-after-update.yml")
  void shouldUpdateEntity() {
    final var entityOptional = itemRepository.findById(1L);
    assertThat(entityOptional).isPresent();

    final var updatedEntity = entityOptional.get();
    updatedEntity.setValue(42);

    itemRepository.saveAndFlush(updatedEntity);
  }


}

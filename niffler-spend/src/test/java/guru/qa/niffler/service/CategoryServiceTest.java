package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Test
  void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
    final String username = "not_found";
    final UUID id = UUID.randomUUID();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.empty());

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "",
        username,
        true
    );

    CategoryNotFoundException ex = Assertions.assertThrows(
        CategoryNotFoundException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t find category by id: '" + id + "'",
        ex.getMessage()
    );
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        catName,
        username,
        true
    );

    InvalidCategoryNameException ex = Assertions.assertThrows(
        InvalidCategoryNameException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t add category with name: '" + catName + "'",
        ex.getMessage()
    );
  }

  @Test
  void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();
    cat.setId(id);
    cat.setUsername(username);
    cat.setName("Магазины");
    cat.setArchived(false);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "Бары",
        username,
        true
    );

    categoryService.update(categoryJson);
    ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(argumentCaptor.capture());
    assertEquals("Бары", argumentCaptor.getValue().getName());
    assertEquals("duck", argumentCaptor.getValue().getUsername());
    assertTrue(argumentCaptor.getValue().isArchived());
    assertEquals(id, argumentCaptor.getValue().getId());
  }

  @Test
  void getAllCategoriesShouldReturnOnlyNonArchivedWhenExcludeArchivedIsTrue(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";

    CategoryEntity archivedCategory = new CategoryEntity();
    archivedCategory.setId(UUID.randomUUID());
    archivedCategory.setName("Архивная категория");
    archivedCategory.setUsername(username);
    archivedCategory.setArchived(true);

    CategoryEntity nonArchivedCategory1 = new CategoryEntity();
    nonArchivedCategory1.setId(UUID.randomUUID());
    nonArchivedCategory1.setName("Еда");
    nonArchivedCategory1.setUsername(username);
    nonArchivedCategory1.setArchived(false);

    CategoryEntity nonArchivedCategory2 = new CategoryEntity();
    nonArchivedCategory2.setId(UUID.randomUUID());
    nonArchivedCategory2.setName("Транспорт");
    nonArchivedCategory2.setUsername(username);
    nonArchivedCategory2.setArchived(false);

    Mockito.when(categoryRepository.findAllByUsernameOrderByName(eq(username)))
            .thenReturn(List.of(archivedCategory, nonArchivedCategory1, nonArchivedCategory2));

    CategoryService categoryService = new CategoryService(categoryRepository);

    List<CategoryJson> result = categoryService.getAllCategories(username, true);

    assertEquals(2, result.size());
    assertTrue(result.stream().noneMatch(CategoryJson::archived));
    assertTrue(result.stream().anyMatch(cat -> "Еда".equals(cat.name())));
    assertTrue(result.stream().anyMatch(cat -> "Транспорт".equals(cat.name())));
  }

  @Test
  void getAllCategoriesShouldReturnAllCategoriesWhenExcludeArchivedIsFalse(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";

    CategoryEntity archivedCategory = new CategoryEntity();
    archivedCategory.setId(UUID.randomUUID());
    archivedCategory.setName("Архивная категория");
    archivedCategory.setUsername(username);
    archivedCategory.setArchived(true);

    CategoryEntity nonArchivedCategory = new CategoryEntity();
    nonArchivedCategory.setId(UUID.randomUUID());
    nonArchivedCategory.setName("Еда");
    nonArchivedCategory.setUsername(username);
    nonArchivedCategory.setArchived(false);

    Mockito.when(categoryRepository.findAllByUsernameOrderByName(eq(username)))
            .thenReturn(List.of(archivedCategory, nonArchivedCategory));

    CategoryService categoryService = new CategoryService(categoryRepository);

    List<CategoryJson> result = categoryService.getAllCategories(username, false);

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(cat -> cat.archived() && "Архивная категория".equals(cat.name())));
    assertTrue(result.stream().anyMatch(cat -> !cat.archived() && "Еда".equals(cat.name())));
  }


  @Test
  void updateShouldThrowTooManyCategoriesExceptionWhenUnarchivingCategoryAndMaxLimitExceeded(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity archivedCategory = new CategoryEntity();
    archivedCategory.setId(id);
    archivedCategory.setUsername(username);
    archivedCategory.setName("Архивная категория");
    archivedCategory.setArchived(true);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
            .thenReturn(Optional.of(archivedCategory));

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(8L);

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryToUpdate = new CategoryJson(
            id,
            "Разархивированная категория",
            username,
            false
    );

    TooManyCategoriesException ex = Assertions.assertThrows(
            TooManyCategoriesException.class,
            () -> categoryService.update(categoryToUpdate)
    );

    assertEquals("Can`t unarchive category for user: '" + username + "'", ex.getMessage());
  }

  @Test
  void updateShouldSuccessfullyUnarchiveCategoryWhenMaxLimitNotExceeded(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity archivedCategory = new CategoryEntity();
    archivedCategory.setId(id);
    archivedCategory.setUsername(username);
    archivedCategory.setName("Архивная категория");
    archivedCategory.setArchived(true);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
            .thenReturn(Optional.of(archivedCategory));

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(7L);

    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryToUpdate = new CategoryJson(
            id,
            "Разархивированная категория",
            username,
            false
    );

    CategoryJson result = categoryService.update(categoryToUpdate);

    assertEquals("Разархивированная категория", result.name());
    assertEquals(username, result.username());
    assertEquals(false, result.archived());
    assertEquals(id, result.id());
  }

  @Test
  void updateShouldSuccessfullyUnarchiveCategoryWhenBoundaryLimit(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity archivedCategory = new CategoryEntity();
    archivedCategory.setId(id);
    archivedCategory.setUsername(username);
    archivedCategory.setName("Архивная категория");
    archivedCategory.setArchived(true);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
            .thenReturn(Optional.of(archivedCategory));

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(7L);

    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryToUpdate = new CategoryJson(
            id,
            "Разархивированная категория",
            username,
            false
    );

    CategoryJson result = categoryService.update(categoryToUpdate);

    assertEquals("Разархивированная категория", result.name());
    assertEquals(false, result.archived());
  }


  @Test
  void saveShouldThrowTooManyCategoriesExceptionWhenMaxLimitExceeded(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(8L);

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryToSave = new CategoryJson(
            null,
            "Новая категория",
            username,
            false
    );

    TooManyCategoriesException ex = Assertions.assertThrows(
            TooManyCategoriesException.class,
            () -> categoryService.addCategory(categoryToSave)
    );

    assertEquals("Can`t add over than 8 categories for user: '" + username + "'", ex.getMessage());

    Mockito.verify(categoryRepository, Mockito.never()).save(any(CategoryEntity.class));
  }

  @Test
  void saveShouldSuccessfullyCreateCategoryWhenMaxLimitNotExceeded(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID generatedId = UUID.randomUUID();

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(6L);

    CategoryEntity savedEntity = new CategoryEntity();
    savedEntity.setId(generatedId);
    savedEntity.setName("Новая категория");
    savedEntity.setUsername(username);
    savedEntity.setArchived(false);

    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenReturn(savedEntity);

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryToSave = new CategoryJson(
            null,
            "Новая категория",
            username,
            false
    );

    CategoryJson result = categoryService.addCategory(categoryToSave);

    assertEquals("Новая категория", result.name());
    assertEquals(username, result.username());
    assertEquals(false, result.archived());
    assertEquals(generatedId, result.id());

    ArgumentCaptor<CategoryEntity> captor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(captor.capture());
    CategoryEntity capturedEntity = captor.getValue();
    assertEquals("Новая категория", capturedEntity.getName());
    assertEquals(username, capturedEntity.getUsername());
    assertEquals(false, capturedEntity.isArchived());
  }

  @Test
  void saveShouldSuccessfullyCreateCategoryWhenBoundaryLimit(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID generatedId = UUID.randomUUID();

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(7L);

    CategoryEntity savedEntity = new CategoryEntity();
    savedEntity.setId(generatedId);
    savedEntity.setName("Граничная категория");
    savedEntity.setUsername(username);
    savedEntity.setArchived(false);

    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenReturn(savedEntity);

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryToSave = new CategoryJson(
            null,
            "Граничная категория",
            username,
            false
    );

    CategoryJson result = categoryService.addCategory(categoryToSave);

    assertEquals("Граничная категория", result.name());
    assertEquals(false, result.archived());
  }
}
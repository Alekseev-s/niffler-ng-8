package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.model.userdata.StaticUser;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

  static {
      EMPTY_USERS.add(new StaticUser("friendless", "12345", null, null, null));
      WITH_FRIEND_USERS.add(new StaticUser("with_fren", "12345", "duck", null, null));
      WITH_INCOME_REQUEST_USERS.add(new StaticUser("with_in_req", "12345", null, "with_out_req", null));
      WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("with_out_req", "12345", null, null, "with_in_req"));
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    Arrays.stream(context.getRequiredTestMethod().getParameters())
        .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class) && p.getType().isAssignableFrom(StaticUser.class))
        .forEach(p -> {
            UserType ut = p.getAnnotation(UserType.class);
            Optional<StaticUser> user = Optional.empty();
            StopWatch sw = StopWatch.createStarted();
            while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                user = switch (ut.value()) {
                    case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                    case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
                    case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
                    case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
                };
            }
            Allure.getLifecycle().updateTestCase(testCase ->
                    testCase.setStart(new Date().getTime())
            );
            user.ifPresentOrElse(u ->
                            ((Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                                    .getOrComputeIfAbsent(
                                            context.getUniqueId(),
                                            key -> new HashMap<>()
                                    )).put(ut, u),
                    () -> {
                        throw new IllegalStateException("Can`t obtain user after 30s.");
                    }
            );
        });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
      Map<UserType, StaticUser> map = context.getStore(NAMESPACE).get(
              context.getUniqueId(),
              Map.class);
      if (map != null) {
          for (Map.Entry<UserType, StaticUser> e : map.entrySet()) {
              UserType ut = e.getKey();
              StaticUser user = e.getValue();
              switch (ut.value()) {
                  case EMPTY -> EMPTY_USERS.add(user);
                  case WITH_FRIEND -> WITH_FRIEND_USERS.add(user);
                  case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(user);
                  case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(user);
              }
          }
      }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
        && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      UserType ut = parameterContext.getParameter().getAnnotation(UserType.class);
      Map<UserType, StaticUser> map = (Map<UserType, StaticUser>) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
      return map.get(ut);
  }
}

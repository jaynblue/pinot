package com.linkedin.thirdeye.taskexecution.impl.dag;

import com.linkedin.thirdeye.taskexecution.dataflow.ExecutionResult;
import com.linkedin.thirdeye.taskexecution.dag.NodeIdentifier;
import com.linkedin.thirdeye.taskexecution.operator.Operator;
import com.linkedin.thirdeye.taskexecution.operator.OperatorConfig;
import com.linkedin.thirdeye.taskexecution.operator.OperatorContext;
import org.testng.Assert;
import org.testng.annotations.Test;


public class OperatorRunnerTest {

  @Test
  public void testCreation() {
    try {
      new OperatorRunner(new NodeIdentifier(), new NodeConfig(), DummyOperator.class);
    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void testSuccessInitializeOperator() throws InstantiationException, IllegalAccessException {
    OperatorRunner.initializeOperator(DummyOperator.class, new OperatorConfig());
  }

  @Test
  public void testFailureInitializeOperator() {
    try {
      OperatorRunner.initializeOperator(FailedInitializedOperator.class, new OperatorConfig());
    } catch (Exception e) {
      return;
    }
    Assert.fail();
  }

  @Test
  public void testSuccessOfOperator() {
    NodeConfig nodeConfig = new NodeConfig();
    nodeConfig.setSkipAtFailure(false);
    nodeConfig.setNumRetryAtError(0);
    OperatorRunner runner = new OperatorRunner(new NodeIdentifier(), nodeConfig, DummyOperator.class);
    runner.call();
    Assert.assertEquals(runner.getExecutionStatus(), ExecutionStatus.SUCCESS);
  }

  @Test
  public void testFailureOfOperator() {
    NodeConfig nodeConfig = new NodeConfig();
    nodeConfig.setSkipAtFailure(false);
    nodeConfig.setNumRetryAtError(1);
    OperatorRunner runner = new OperatorRunner(new NodeIdentifier(), nodeConfig, FailedRunOperator.class);
    runner.call();
    Assert.assertEquals(runner.getExecutionStatus(), ExecutionStatus.FAILED);
  }

  @Test
  public void testSkippedOfOperator() {
    NodeConfig nodeConfig = new NodeConfig();
    nodeConfig.setSkipAtFailure(true);
    nodeConfig.setNumRetryAtError(2);
    OperatorRunner runner = new OperatorRunner(new NodeIdentifier(), nodeConfig, FailedRunOperator.class);
    runner.call();
    Assert.assertEquals(runner.getExecutionStatus(), ExecutionStatus.SKIPPED);
  }

  static class DummyOperator implements Operator {
    @Override
    public void initialize(OperatorConfig operatorConfig) {
    }

    @Override
    public ExecutionResult run(OperatorContext operatorContext) {
      return new ExecutionResult();
    }
  }

  static class FailedInitializedOperator implements Operator {
    @Override
    public void initialize(OperatorConfig operatorConfig) {
      throw new UnsupportedOperationException("Failed during initialization IN PURPOSE.");
    }

    @Override
    public ExecutionResult run(OperatorContext operatorContext) {
      return new ExecutionResult();
    }
  }

  static class FailedRunOperator implements Operator {
    @Override
    public void initialize(OperatorConfig operatorConfig) {
    }

    @Override
    public ExecutionResult run(OperatorContext operatorContext) {
      throw new UnsupportedOperationException("Failed during running IN PURPOSE.");
    }
  }
}

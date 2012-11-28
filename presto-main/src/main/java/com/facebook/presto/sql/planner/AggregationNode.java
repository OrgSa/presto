package com.facebook.presto.sql.planner;

import com.facebook.presto.metadata.FunctionHandle;
import com.facebook.presto.sql.compiler.Symbol;
import com.facebook.presto.sql.tree.FunctionCall;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.concat;

public class AggregationNode
    extends PlanNode
{
    private final PlanNode source;
    private final List<Symbol> groupByKeys;
    private final Map<Symbol, FunctionCall> aggregations;
    private final Map<Symbol, FunctionHandle> functions;
    private final Step step;

    public enum Step {
        PARTIAL,
        FINAL,
        SINGLE
    }

    public AggregationNode(PlanNode source, List<Symbol> groupByKeys, Map<Symbol, FunctionCall> aggregations, Map<Symbol, FunctionHandle> functions)
    {
        this(source, groupByKeys, aggregations, functions, Step.SINGLE);
    }

    public AggregationNode(PlanNode source,
            List<Symbol> groupByKeys,
            Map<Symbol, FunctionCall> aggregations,
            Map<Symbol, FunctionHandle> functions,
            Step step)
    {
        this.source = source;
        this.groupByKeys = groupByKeys;
        this.aggregations = aggregations;
        this.functions = functions;
        this.step = step;
    }

    @Override
    public List<PlanNode> getSources()
    {
        return ImmutableList.of(source);
    }

    @Override
    public List<Symbol> getOutputSymbols()
    {
        return ImmutableList.copyOf(concat(groupByKeys, aggregations.keySet()));
    }

    public Map<Symbol, FunctionCall> getAggregations()
    {
        return aggregations;
    }

    public Map<Symbol, FunctionHandle> getFunctions()
    {
        return functions;
    }

    public List<Symbol> getGroupBy()
    {
        return groupByKeys;
    }

    public PlanNode getSource()
    {
        return source;
    }

    public Step getStep()
    {
        return step;
    }

    public <C, R> R accept(PlanVisitor<C, R> visitor, C context)
    {
        return visitor.visitAggregation(this, context);
    }
}

package de.hpi.isg.dataprep.preparators.implementation;

import de.hpi.isg.dataprep.ExecutionContext;
import de.hpi.isg.dataprep.components.PreparatorImpl;
import de.hpi.isg.dataprep.model.error.PreparationError;
import de.hpi.isg.dataprep.model.target.system.AbstractPreparator;
import de.hpi.isg.dataprep.preparators.define.Sampling;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.util.CollectionAccumulator;

import java.util.Random;
import java.util.stream.IntStream;

public class DefaultSamplingImpl extends PreparatorImpl {

    @Override
    protected ExecutionContext executeLogic(AbstractPreparator abstractPreparator, Dataset<Row> dataFrame, CollectionAccumulator<PreparationError> errorAccumulator) throws Exception {

        Sampling sampler = (Sampling) abstractPreparator;
        Random rand = new Random();
        return new ExecutionContext(dataFrame.filter((Row x) -> rand.nextInt(100) < sampler.getPercentage() *100 ),errorAccumulator); //was wenn ungleichmäßig verteilt?

        // Order by top 5
        // return new ExecutionContext(dataFrame.head(5),errorAccumulator);

    }
}

/*
//Order By Rand()

assign random number to every element
keep top 10


//Sample Size 10

    Keep the first ten items in memory.
        When the i-th item arrives (for  i>10):
        with probability 10/i, keep the new item (discard an old one, selecting which to replace at random, each with chance 1/10)
        with probability 1-10/i, keep the old items (ignore the new one)

//Algorithmn R

(*
  S has items to sample, R will contain the result
 *)
ReservoirSample(S[1..n], R[1..k])
  // fill the reservoir array
  for i = 1 to k
      R[i] := S[i]

  // replace elements with gradually decreasing probability
  for i = k+1 to n
    j := random(1, i)   // important: inclusive range
    if j <= k
        R[j] := S[i]

//Reservoir Sampling with Random Sort

(*
  S is a stream of items to sample, R will contain the result
  S.Current returns current item in stream
  S.Next advances stream to next position
  min-priority-queue supports:
    Count -> number of items in priority queue
    Minimum -> returns minimum key value of all items
    Extract-Min() -> Remove the item with min key
    Insert(key, Item) -> Adds item with specified key
 *)
ReservoirSample(S[1..?], R[1..k])
  H = new min-priority-queue
  while S has data
    r = Random(0,1)
    if H.Count < k
      H.Insert(r, S.Current)
    else
      if H.Minimum < r
        H.Extract-Min()
        H.Insert(r, S.Current)
    S.Next


//The Sampling Algorithmn by Goodman and Hedetniemi

We denote by E the set of unselected elements and by last the number of the elements in E. Initially let E = E°
and last = n. Further let operartor o(i) give the E°-index of the fth element of E. Thus initially we have o(0 = i, for
i=l,2,... , last.
The first sample element is found by choosing a random integer zElement[l, last] and writing the index o(z)
down into the array s. The element chosen is e with index o(z), and it is deleted from E. The value of o(z) is redefined
to o(last) and last is reduced by one. The same actions are then repeated for each sample element
 */
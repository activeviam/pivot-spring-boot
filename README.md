
<h1 align="center">ActivePivot Benchmark Application</h1>

---

## Branch info 

Branch for the application displaying the CI/CD'ed benchmark results.

## 📋 Queries 

#### QUERY 1 : displays the worst performance regressions between two AP Versions 

    
    WITH
     Member [Measures].[DIFF] AS (
      [Measures].[V1],
      [Session].[Code Version].[5.11.0-SNAPSHOT]
    ) - (
      [Measures].[V1],
      [Session].[Code Version].[5.10.5-SNAPSHOT]
    ), FORMAT = "#,###.00"  
     Member [Measures].[RATIO_IN_PERCENTAGE] AS Divide(
      [Measures].DIFF,
      (
        [Measures].[V1],
        [Session].[Code Version].[5.10.5-SNAPSHOT]
      )
    ) * 100, FORMAT = "#,###.00"  
     Member [Measures].[SLOWER] AS IIF(
      [Measures].[RATIO_IN_PERCENTAGE] > 5,
      [Measures].[RATIO_IN_PERCENTAGE],
      NULL
    ), FORMAT = "#,###.00"  
     Member [Measures].[5.10Val] AS (
      [Measures].[V1],
      [Session].[Code Version].[5.10.5-SNAPSHOT]
    )  
     Member [Measures].[5.11Val] AS (
      [Measures].[V1],
      [Session].[Code Version].[5.11.0-SNAPSHOT]
    ) 
    SELECT
      NON EMPTY {
        [Measures].[SLOWER],
        [Measures].[5.11Val],
        [Measures].[5.10Val]
      } ON COLUMNS,
      NON EMPTY Order(
        Crossjoin(
          [Benchmark].[Benchmark].[Plugin key].Members,
          [Parameters].[P1].[AllMember].Children,
          [Parameters].[P2].[AllMember].Children,
          [Parameters].[P3].[AllMember].Children,
          [Parameters].[P4].[AllMember].Children,
          [Parameters].[P5].[AllMember].Children,
          [Parameters].[P6].[AllMember].Children,
          [Parameters].[P7].[AllMember].Children,
          [Parameters].[P8].[AllMember].Children
        ),
        [Measures].[SLOWER],
        BDESC
      ) ON ROWS
      FROM (
        SELECT
        {
          [Session].[Code Version].[AllMember].[5.10.5-SNAPSHOT],
          [Session].[Code Version].[AllMember].[5.11.0-SNAPSHOT]
        } ON COLUMNS
        FROM [Cube]
      )

**QUERY 2 : displays the trend of a given Benchmark Execution on the 5.11 Version**
 - Use ActiveUI's area chart, and set "P1" as Horizontal subplot , "P2" as Vertical subplot.
 - Replace QFSBitmap as the PluginKey of the tested benchmark
 - Replace 5.11 to replace the branch for which you want to follow
```
SELECT
  NON EMPTY Crossjoin(
    Hierarchize(
      Descendants(
        {
          [Session].[Code Version].[AllMember]
        },
        1,
        SELF_AND_BEFORE
      )
    ),
    {
      [Measures].[V1_E],
      [Measures].[V1]
    },
    Hierarchize(
      Descendants(
        {
          [Parameters].[P3].[AllMember]
        },
        1,
        SELF_AND_BEFORE
      )
    ),
    Hierarchize(
      Descendants(
        {
          [Parameters].[P4].[AllMember]
        },
        1,
        SELF_AND_BEFORE
      )
    ),
    Hierarchize(
      Descendants(
        {
          [Parameters].[P6].[AllMember]
        },
        1,
        SELF_AND_BEFORE
      )
    ),
    Hierarchize(
      Descendants(
        {
          [Parameters].[P1].[AllMember]
        },
        1,
        SELF_AND_BEFORE
      )
    ),
    Hierarchize(
      Descendants(
        {
          [Parameters].[P2].[AllMember]
        },
        1,
        SELF_AND_BEFORE
      )
    )
  ) ON COLUMNS,
  NON EMPTY Hierarchize(
    Descendants(
      {
        [Session].[Execution time].[AllMember]
      },
      1,
      SELF_AND_BEFORE
    )
  ) ON ROWS
  FROM (
    SELECT
    [Session].[Code Version].[AllMember].[5.11] ON COLUMNS
    FROM [Cube]
    WHERE [Benchmark].[Benchmark].[AllMember].[QFSBitmap]
  )
  WHERE [Benchmark].[Benchmark].[AllMember].[QFSBitmap]
  CELL PROPERTIES VALUE, FORMATTED_VALUE, BACK_COLOR, FORE_COLOR, FONT_FLAGS
```

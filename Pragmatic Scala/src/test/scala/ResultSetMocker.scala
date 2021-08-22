import java.sql.ResultSet

import org.mockito.Mockito

class ResultSetMocker {

  val resultSetMock= Mockito.mock(classOf[ResultSet])

  import org.mockito.Mockito

  Mockito.when(resultSetMock.getString("trade_date")).thenReturn("03/10/2011")
  Mockito.when(resultSetMock.getString("trade_time")).thenReturn("12:24:56")
  Mockito.when(resultSetMock.getString("expr_date")).thenReturn("03/19/2011")
  Mockito.when(resultSetMock.getString("symbol")).thenReturn("VIX1")

}

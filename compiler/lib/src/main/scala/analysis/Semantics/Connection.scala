package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP connection */
case class Connection(
  /** The from endpoint */
  from: Connection.Endpoint,
  /** The to endpoint */
  to: Connection.Endpoint
) extends Ordered[Connection] {

  /** Checks the types of a connection */
  def checkTypes: Result.Result[Unit] = {
    val fromInstance = from.portInstanceIdentifier.portInstance
    val fromType = fromInstance.getType
    val toInstance = to.portInstanceIdentifier.portInstance
    val toType = toInstance.getType
    if (PortInstance.Type.areCompatible(fromType, toType)) 
      Right(())
    else {
      val fromTypeString = PortInstance.Type.show(fromType)
      val toTypeString = PortInstance.Type.show(toType)
      val msg = s"cannot connect port types $fromTypeString and $toTypeString"
      val fromLoc = fromInstance.getLoc
      val toLoc = toInstance.getLoc
      Left(SemanticError.InvalidConnection(getLoc, msg, fromLoc, toLoc))
    }
  }

  /** Checks the directions of a connection */
  def checkDirections: Result.Result[Unit] = {
    val fromInstance = from.portInstanceIdentifier.portInstance
    val fromDirection = fromInstance.getDirection
    val toInstance = to.portInstanceIdentifier.portInstance
    val toDirection = toInstance.getDirection
    if (PortInstance.Direction.areCompatible(fromDirection -> toDirection)) 
      Right(())
    else {
      val fromDirString = PortInstance.Direction.show(fromDirection)
      val toDirString = PortInstance.Direction.show(toDirection)
      val msg = s"invalid directions $fromDirString -> $toDirString (should be output -> input)"
      val fromLoc = fromInstance.getLoc
      val toLoc = toInstance.getLoc
      Left(SemanticError.InvalidConnection(getLoc, msg, fromLoc, toLoc))
    }
  }

  /** Compare two connections */
  def compare(that: Connection) = {
    val fromCompare = this.from.compare(that.from)
    if (fromCompare != 0) fromCompare
    else this.to.compare(that.to)
  }

  /** Gets the location of the connection */
  def getLoc: Location = from.loc

}

object Connection {

  /** Constructs a connection from an AST connection */
  def fromAst(a: Analysis, connection: Ast.SpecConnectionGraph.Connection):
    Result.Result[Connection] = {
      val loc = Locations.get(connection.fromPort.id)
      for {
        from <- Endpoint.fromAst(a, connection.fromPort, connection.fromIndex)
        to <- Endpoint.fromAst(a, connection.toPort, connection.toIndex)
        connection <- Right(Connection(from, to))
        _ <- connection.checkTypes
        _ <- connection.checkDirections
      }
      yield connection
  }

  /** A connection endpoint */
  case class Endpoint(
    /** The location where the endpoint is defined */
    loc: Location,
    /** The port instance identifier */
    portInstanceIdentifier: PortInstanceIdentifier,
    /** The port number */
    portNumber: Option[Int] = None
  ) extends Ordered[Endpoint] {

    def compare(that: Endpoint) = {
      val name1 = this.portInstanceIdentifier.getQualifiedName.toString
      val name2 = that.portInstanceIdentifier.getQualifiedName.toString
      val nameCompare = name1.compare(name2)
      if (nameCompare != 0) nameCompare
      else (this.portNumber, that.portNumber) match {
        case (Some (n1), Some (n2)) => n1 - n2
        case _ => 0
      }
    }

    /** Check that port number is in bounds for the size */
    def checkPortNumber(loc: Location): Result.Result[Unit] = portNumber match {
      case Some(n) =>
        val size = portInstanceIdentifier.portInstance.getSize
        val specLoc = portInstanceIdentifier.portInstance.getLoc
        val name = portInstanceIdentifier.getQualifiedName.toString
        if (n < size) Right(())
        else Left(SemanticError.InvalidPortNumber(loc, n, name, size, specLoc))
      case None => Right(())
    }

  }

  object Endpoint {

    /** Constructs a connection endpoint from AST info */
    def fromAst(
      a: Analysis,
      port: AstNode[Ast.PortInstanceIdentifier],
      portNumber: Option[AstNode[Ast.Expr]]
    ): Result.Result[Endpoint] = for {
      pid <- PortInstanceIdentifier.fromNode(a, port)
      _ <- pid.portInstance.requireConnectionAt(Locations.get(port.id))
      pn <- a.getIntValueOpt(portNumber)
      endpoint <- Right(Endpoint(Locations.get(port.id), pid, pn))
      _ <- portNumber match {
        case Some(pn) => endpoint.checkPortNumber(Locations.get(pn.id))
        case None => Right(())
      }
    } yield endpoint

    /** The lexical ordering on endpoints */
    object LexicalOrdering extends Ordering[Endpoint] {

      def compare(e1: Endpoint, e2: Endpoint) = {
        val name1 = e1.portInstanceIdentifier.getQualifiedName.toString
        val name2 = e2.portInstanceIdentifier.getQualifiedName.toString
        if (name1 < name2) -1
        else if (name1 > name2) 1
        else (e1.portNumber, e2.portNumber) match {
          case (Some (n1), Some (n2)) => n1 - n2
          case _ => 0
        }
      }

    }


  }

}
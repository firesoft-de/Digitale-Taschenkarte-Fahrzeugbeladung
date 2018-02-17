Imports System.IO
Imports System.Runtime.InteropServices
Imports Microsoft.Office.Interop
Imports Microsoft.Office.Interop.Excel

Module Module1

    Dim exApp As Excel.Application
    Dim exBook As Excel.Workbook
    Dim exSheet As Excel.Worksheet

    Dim exportString As String

    Sub Main()

        Console.WriteLine("Excel Datei öffnen und mit Enter bestätigen!")
        Console.Read()

        initaliseExcel()

        Console.WriteLine("Excel wurde erfolgreich initalisiert!")
        Console.Write("Aktuelle Datei: ")
        Console.WriteLine(exApp.Name)
        Console.Write("Aktuelle Arbeitsbuch: ")
        Console.WriteLine(exBook.Name)

        exSheet = CType(exBook.ActiveSheet, Worksheet)

        Console.Write("Aktuelle Tabelle: ")
        Console.WriteLine(exSheet.Name)

        Console.WriteLine("Jetzt Datenbanktabelle öffnen und mit Enter bestätigen! Der nächste Schritt kann einen Moment dauern.")
        Console.Read()

        gatherDatabaseEntries()

        Console.WriteLine("Daten werden jetzt gespeichert!")

        saveDatabase()

        Console.WriteLine("Daten gespeichert!")

        Console.Read()

    End Sub

    Sub initaliseExcel()

        Try

            exApp = CType(Marshal.GetActiveObject("Excel.Application"), Excel.Application)
            exBook = exApp.ActiveWorkbook
            exApp.Visible = True

        Catch ex As Exception

            exApp = New Excel.Application()
            exBook = exApp.Workbooks.Add()
            exApp.Visible = True

        End Try

        exSheet = CType(exApp.ActiveSheet, Worksheet)

    End Sub

    Sub gatherDatabaseEntries()

        Dim rows As Integer = exSheet.Cells.Find(What:="*", After:=exSheet.Range("A1"),
                             SearchOrder:=Excel.XlSearchOrder.xlByRows,
                             SearchDirection:=Excel.XlSearchDirection.xlPrevious).Row

        Dim cols As Integer = exSheet.Cells.Find(What:="*", After:=exSheet.Range("A1"),
                             SearchOrder:=Excel.XlSearchOrder.xlByColumns,
                             SearchDirection:=Excel.XlSearchDirection.xlPrevious).Column

        ''Eine Spalte abziehen, da die Anmerkungsspalte nicht mitgezählt werden soll
        cols -= 1

        Console.Write(rows.ToString)
        Console.WriteLine(" Zeile/Zeilen erkannt!")

        Console.Write(cols.ToString)
        Console.WriteLine(" Spalte/Spalten erkannt!")

        Console.WriteLine("Exportmethode angeben! (1 - klassische CSV, 2 - für phpmyadmin angepasste CSV)!")

        Dim cont As Boolean = False

        While cont = False

            Dim mark As String = Console.ReadLine.ToString

            exportString = ""

            If (mark = "1") Then
                exportString = classicCSV(rows, cols)
                cont = True
            ElseIf (mark = "2") Then
                exportString = modifiedCSV(rows, cols)
                cont = True
            End If

        End While


    End Sub

    Function modifiedCSV(ByVal rows As Integer, ByVal cols As Integer)

        Dim r As Range
        Dim result As String = ""

        For currentrow = 1 To rows

            Dim s As String = ""

            For currentcol = 1 To cols

                Dim exRng As Excel.Range = CType(exSheet.Cells(currentrow, currentcol), Excel.Range)
                If (exRng.Value() IsNot Nothing) Then

                    s = s.Replace("\""", """""")

                    s &= """" & exRng.Value().ToString & """"
                Else
                    s &= """" & """"
                End If
                s &= ","

            Next

            s = s.Remove(s.Length - 1, 1)

            result &= s
            result &= Environment.NewLine

        Next

        result = result.Remove(result.Length - 2, 2)

        Return result

    End Function

    Function classicCSV(ByVal rows As Integer, ByVal cols As Integer)

        Dim r As Range
        Dim result As String = ""

        For currentrow = 1 To rows

            Dim s As String = ""

            For currentcol = 1 To cols

                Dim exRng As Excel.Range = CType(exSheet.Cells(currentrow, currentcol), Excel.Range)
                If (exRng.Value() IsNot Nothing) Then
                    s &= exRng.Value().ToString
                Else
                    s &= ""
                End If
                s &= ";"

            Next

            s = s.Remove(s.Length - 1, 1)

            result &= s
            result &= Environment.NewLine

        Next
        result = result.Remove(result.Length - 2, 2)

        Return result

    End Function

    Sub saveDatabase()

        Dim path As String = My.Application.Info.DirectoryPath
        path &= "\export.csv"

        If (IO.File.Exists(path)) Then
            IO.File.Delete(path)
        End If

        Dim stream As New FileStream(path, FileMode.CreateNew)

        Dim writer As New StreamWriter(stream, Text.Encoding.UTF8)
        writer.WriteLine(exportString)

        writer.Close()

    End Sub

End Module

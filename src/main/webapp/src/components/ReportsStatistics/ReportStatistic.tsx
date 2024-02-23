import React from "react";
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'
import ExcelJS from "exceljs";
import PDFLine from "../../classes/PDFLine";
import { useSelector } from "react-redux";
import { RootState } from "../../store";
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";
import { Button } from '@mantine/core';

interface DocumentReport {
  name: string;
  ruleViolations: RuleViolation[];
  errorCode: number;
}

interface RuleViolation {
  lines: PDFLine[];
  message: string;
  type: string;
}

interface FileErrors {
  [fileName: string]: { [errorMessage: string]: number };
}

interface ErrorCountProps {
  reports: DocumentReport[];
}

const ErrorCount: React.FC<ErrorCountProps> = ({  }) => {
  const rulesFul = useSelector((state: RootState) => state.file.ruleSet);
  const reports = useSelector((state: RootState) => state.file.fullPerort);
  const countErrors = (reports: DocumentReport[]): FileErrors => {
    const fileErrors: FileErrors = {};

    reports.forEach((report) => {
      const { name, ruleViolations } = report;

      ruleViolations.forEach((violation) => {
        const { message } = violation;

        if (fileErrors[name]) {
          if (fileErrors[name][message]) {
            fileErrors[name][message]++;
          } else {
            fileErrors[name][message] = 1;
          }
        } else {
          fileErrors[name] = { [message]: 1 };
        }
      });
    });

    return fileErrors;
  };

  const truncateFileName = (fileName: string): string => {
    if (fileName.length > 50) {
      return fileName.substring(0, 10) + "...";
    }
    return fileName;
  };

  const fileErrors = countErrors(reports);

  const downloadTableAsPDF = () => {
    pdfMake.vfs = pdfFonts.pdfMake.vfs;
    let docDefinition: any;
    docDefinition = {
      content: [
        { text: "Статистика отчетов", style: "header" },
        {
          table: {
            headerRows: 1,
            body: [
              ["Имя файла", ...Object.keys(fileErrors).flatMap((fileName) => [truncateFileName(fileName)])],
              ...Object.keys(fileErrors[Object.keys(fileErrors)[0]]).map((errorMessage, errorIndex) => [
                errorMessage,
                ...Object.keys(fileErrors).flatMap((fileName) => [fileErrors[fileName][errorMessage] || 0]),
              ]),
            ],
          },
          layout: "lightHorizontalLines",
        },
      ],
      styles: {
        header: {
          fontSize: 18,
          bold: true,
          margin: [0, 0, 0, 10],
        },
      },
    };

    pdfMake.createPdf(docDefinition).download("error-table.pdf");
  };

  const downloadTableAsExcel = () => {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet("Статистика отчетов");

    const columnHeaders = ["Имя файла", ...Object.keys(fileErrors).map((fileName) => truncateFileName(fileName))];
    worksheet.getRow(1).values = columnHeaders;
    worksheet.getRow(1).font = { bold: true };

    const errorMessages = Object.keys(fileErrors[Object.keys(fileErrors)[0]]);
    errorMessages.forEach((errorMessage, errorIndex) => {
      const rowValues = [errorMessage, ...Object.keys(fileErrors).map((fileName) => fileErrors[fileName][errorMessage] || 0)];
      worksheet.addRow(rowValues);
    });

    workbook.xlsx.writeBuffer().then((buffer: any) => {
      const blob = new Blob([buffer], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "error-table.xlsx";
      link.click();
    });
  };

  return (
    <div>
      <h2>Статистика отчетов</h2>
      <Button onClick={downloadTableAsPDF} style={{ marginRight: "8px", marginBottom: "5px"}}>
        Скачать PDF
      </Button>
      <Button onClick={downloadTableAsExcel}>Скачать Excel</Button>
      <div style={{ overflow: "auto", maxHeight: "800px", maxWidth: "1400px" }}>
        <table id="error-table" style={{ borderCollapse: "collapse", tableLayout: "fixed" }}>
          <thead>
            <tr>
              <th style={{ border: "1px solid black", padding: "8px" }}>Имя файла</th>
              {Object.keys(fileErrors).map((fileName) => (
                <th key={fileName} style={{ border: "1px solid black", padding: "8px" }}>
                  {truncateFileName(fileName)}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {Object.keys(fileErrors[Object.keys(fileErrors)[0]]).map((errorMessage, errorIndex) => (
              <tr key={`error-${errorIndex}`}>
                <td style={{ border: "1px solid black", padding: "8px" }}>{errorMessage}</td>
                {Object.keys(fileErrors).map((fileName) => (
                  <td key={`${fileName}-${errorMessage}`} style={{ border: "1px solid black", padding: "8px" }}>
                    {fileErrors[fileName][errorMessage] || 0}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
  
};

export default ErrorCount;

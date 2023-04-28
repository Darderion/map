import unittest
import pandas
import contextlib
from pathlib import Path
import io
import os
import sys
import src.main.python.camelot
from src.main.python.TableExtractionScript import extraction

sys.path.insert(0, '../src')

class TableExtractionScriptTest(unittest.TestCase):

    def test_open_file(self):
        pdf_path = 'src/test//resources/com/github/darderion/mundaneassignmentpolice/python/tableextractionscript/OpenNotPDF.docx'

        s = io.StringIO()
        with contextlib.redirect_stdout(s):
            extraction(pdf_path)

        self.assertEqual('invalid PDF file\n', s.getvalue())

    def test_check_table_directory(self):
        pdf_path = 'src/test/resources/com/github/darderion/mundaneassignmentpolice/python/tableextractionscript/TableInformation.pdf'
        extraction(pdf_path)
        self.assertTrue(os.path.exists(f'uploads/tables/{Path(pdf_path).stem}'))

    def test_save_table(self):
        pdf_path = 'src/test/resources/com/github/darderion/mundaneassignmentpolice/python/tableextractionscript/TableInformation.pdf'
        extraction(pdf_path)
        self.assertTrue(os.path.exists('uploads/tables/TableInformation/TableInformation-page-1-table-1.csv'))

    def test_check_table_information(self):
        pdf_path = 'src/test/resources/com/github/darderion/mundaneassignmentpolice/python/tableextractionscript/TableInformation.pdf'
        extraction(pdf_path)
        table = pandas.read_csv(os.path.expanduser("~/map/uploads/tables/TableInformation/TableInformation-page-1-table-1.csv"))
        camelot_table = src.main.python.camelot.read_pdf(pdf_path, linescale=30)[0]
        self.assertEqual('table data', table.columns[0])

        self.assertEqual('table information', table['table data'][4])

        self.assertEqual('page', table['table data'][5])
        self.assertEqual('1', table['table data'][6])

        self.assertEqual('table area', table['table data'][7])
        self.assertEqual(camelot_table.cells[3][0].x1, float(table['table data'][8]))
        self.assertEqual(camelot_table.cells[3][3].x2, float(table['table data'][10]))
        self.assertEqual(camelot_table.cells[3][0].y1, float(table['table data'][9]))
        self.assertEqual(camelot_table.cells[0][3].y2, float(table['table data'][11]))

        self.assertEqual('rows', table['table data'][12])
        self.assertEqual('4', table['table data'][13])

        self.assertEqual('columns', table['table data'][14])
        self.assertEqual('4', table['table data'][15])


if __name__ == '__main__':
    unittest.main()

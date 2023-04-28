import os
import unittest
import sys
sys.path.insert(0, '../src')
import src.main.python.camelot as camelot
from src.main.python.camelot.image_processing import (
    intersectes
)
os.chdir(os.path.expanduser("~/map/src/test/resources/com/github/darderion/mundaneassignmentpolice/python/camelot"))


class DrawingLines(unittest.TestCase):
    def test_v_draw(self):
        file_name = 'DrawingVerticalLines.pdf'

        tables = camelot.read_pdf(file_name, latice=True, pages='1')
        self.assertEqual(0, len(tables))

        tables = camelot.read_pdf(file_name, latice=True, pages='2')
        self.assertEqual(0, len(tables))

        tables = camelot.read_pdf(file_name, latice=True, pages='3')
        self.assertEqual(1, len(tables))
        self.assertEqual(5, len(tables[0].cells))
        self.assertEqual(1, len(tables[0].cols))
        self.assertEqual(5, len(tables[0].rows))

        tables = camelot.read_pdf(file_name, latice=True, pages='4')
        self.assertEqual(3, len(tables))

        self.assertEqual(2, len(tables[0].cells))
        self.assertEqual(2, len(tables[1].cells))
        self.assertEqual(2, len(tables[2].cells))

        self.assertEqual(1, len(tables[0].cols))
        self.assertEqual(1, len(tables[1].cols))
        self.assertEqual(1, len(tables[2].cols))

        self.assertEqual(2, len(tables[0].rows))
        self.assertEqual(2, len(tables[1].rows))
        self.assertEqual(2, len(tables[2].rows))

    def test_h_draw(self):
        file_name = 'DrawingHorizontalLines.pdf'

        tables = camelot.read_pdf(file_name, latice=True, pages='1')
        self.assertEqual(0, len(tables))

        tables = camelot.read_pdf(file_name, latice=True, pages='2')
        self.assertEqual(0, len(tables))

        tables = camelot.read_pdf(file_name, latice=True, pages='3')
        self.assertEqual(1, len(tables))

        tables = camelot.read_pdf(file_name, latice=True, pages='4')
        self.assertEqual(1, len(tables))

        tables = camelot.read_pdf(file_name, latice=True, pages='5')
        self.assertEqual(2, len(tables))

    def test_intersects(self):
        # rib1 intersects rib2 at first end
        rib1, rib2 = (1, 100, 1, 5), (1, 5, 100, 5)
        self.assertEqual(True, intersectes(rib1, rib2))

        # rib1 intersects rib2 at second end
        rib1, rib2 = (1, 100, 100, 100), (100, 100, 100, 5)
        self.assertEqual(True, intersectes(rib1, rib2))

        # horizontal rib1 parallel to horizontal rib2
        rib1, rib2 = (1, 100, 5, 100), (1, 200, 5, 200)
        self.assertEqual(False, intersectes(rib1, rib2))

        # vertical rib1 parallel to vertical rib2
        rib1, rib2 = (1, 100, 1, 200), (10, 100, 10, 200)
        self.assertEqual(False, intersectes(rib1, rib2))

        # rib1 intersects rib2 inside
        rib1, rib2 = (1, 5, 100, 5), (50, 100, 50, 2)
        self.assertEqual(True, intersectes(rib1, rib2))

        # rib1 does not intersect rib2
        rib1, rib2 = (5, 10, 100, 10), (50, 60, 50, 40)
        self.assertEqual(False, intersectes(rib1, rib2))

        # rib1 lies on the same line as rib2 and does not intersect rib2
        rib1, rib2 = (5, 10, 100, 10), (150, 10, 160, 10)
        self.assertEqual(False, intersectes(rib1, rib2))

    def test_correct_lines(self):
        file_name = 'DrawingComplexTables.pdf'

        tables = camelot.read_pdf(file_name, latice=True, pages='1')
        self.assertEqual(1, len(tables))

        tables = camelot.read_pdf(file_name, latice=True, pages='2')
        self.assertEqual(2, len(tables))


        tables = camelot.read_pdf(file_name, latice=True, pages='3')
        self.assertEqual(2, len(tables))

        tables = camelot.read_pdf(file_name, latice=True, pages='4')
        self.assertEqual(3, len(tables))



if __name__ == '__main__':
    unittest.main()

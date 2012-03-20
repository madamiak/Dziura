# Makefile do generowania pdf

# Jak kto woli
LATEX=pdflatex
LATEX_FILES := $(wildcard *.tex)
OBJ_CLEAN=rm -f *.log *.aux *.out *.snm *.toc *.nav *.vrb rys/*.pdf

all: dziura.pdf

dziura.pdf: $(LATEX_FILES)
	$(LATEX) dziura.tex
	$(LATEX) dziura.tex # dla pewności
	$(OBJ_CLEAN)

clean:
	$(OBJ_CLEAN)
	rm -f dziura.pdf

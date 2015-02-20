% number of different possible secrets
syms k;
cPosition = symsum(nchoosek(9,k), 3, 7);
cColor = symsum(nchoosek(6,k), 2, 4);
cLetter = symsum(nchoosek(26,k), 4, 12);

secretPoss = cPosition * cColor * cLetter

% number of different possible challenge matrices
syms l;
cMatrixColor = nchoosek(6,1);
cMatrixLetter = nchoosek(26,1);

matrixPoss = 2 * cMatrixColor * cMatrixLetter * symsum(l*nchoosek(9,l), 3, 6)








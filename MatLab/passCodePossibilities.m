syms n;
cPosition = symsum(nchoosek(9,n), 3, 7);
cColor = symsum(nchoosek(6,n), 2, 4);
cLetter = symsum(nchoosek(26,n), 4, 12);


possibilities = cPosition * cColor * cLetter

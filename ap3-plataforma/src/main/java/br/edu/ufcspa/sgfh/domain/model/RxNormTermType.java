package br.edu.ufcspa.sgfh.domain.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * Tipos de termo (TTY) do RxNorm, conforme o Apêndice 5 da documentação da NLM
 * (https://www.nlm.nih.gov/research/umls/rxnorm/docs/appendix5.html). O tipo diz o quanto o
 * conceito é específico: só o princípio ativo, com concentração, com forma farmacêutica, com marca.
 * Os exemplos são os da própria NLM, em inglês, como aparecem nas respostas do RxNav.
 */
public enum RxNormTermType {

    IN("Princípio ativo", "Ingredient",
            "Substância que dá ao medicamento seu efeito clínico.",
            "Fluoxetine"),
    PIN("Princípio ativo preciso", "Precise Ingredient",
            "Forma específica do princípio ativo, geralmente um sal ou isômero.",
            "Fluoxetine Hydrochloride"),
    MIN("Associação de princípios ativos", "Multiple Ingredients",
            "Dois ou mais princípios ativos na mesma preparação.",
            "Fluoxetine / Olanzapine"),
    SCDC("Componente clínico", "Semantic Clinical Drug Component",
            "Princípio ativo + concentração.",
            "Fluoxetine 4 MG/ML"),
    SCDF("Forma clínica", "Semantic Clinical Drug Form",
            "Princípio ativo + forma farmacêutica.",
            "Fluoxetine Oral Solution"),
    SCDFP("Forma clínica precisa", "Semantic Clinical Drug Form Precise",
            "Princípio ativo preciso + forma farmacêutica.",
            "warfarin sodium Oral Tablet"),
    SCDG("Grupo clínico", "Semantic Clinical Drug Group",
            "Princípio ativo + grupo de forma farmacêutica.",
            "Fluoxetine Oral Product"),
    SCDGP("Grupo clínico preciso", "Semantic Clinical Drug Form Group Precise",
            "Princípio ativo preciso + grupo de forma farmacêutica.",
            "warfarin sodium Pill"),
    SCD("Medicamento clínico", "Semantic Clinical Drug",
            "Princípio ativo + concentração + forma farmacêutica, sem marca.",
            "Fluoxetine 4 MG/ML Oral Solution"),
    GPCK("Embalagem genérica", "Generic Pack",
            "Kit com mais de um medicamento sem marca, cada um com quantidade, concentração e forma.",
            "{11 (varenicline 0.5 MG Oral Tablet) / 42 (varenicline 1 MG Oral Tablet)} Pack"),
    BN("Nome comercial", "Brand Name",
            "Marca de uma família de produtos com o mesmo princípio ativo.",
            "Prozac"),
    SBDC("Componente de marca", "Semantic Branded Drug Component",
            "Princípio ativo + concentração + marca.",
            "Fluoxetine 4 MG/ML [Prozac]"),
    SBDF("Forma de marca", "Semantic Branded Drug Form",
            "Princípio ativo + forma farmacêutica + marca.",
            "Fluoxetine Oral Solution [Prozac]"),
    SBDFP("Forma de marca precisa", "Semantic Branded Drug Form Precise",
            "Princípio ativo preciso + forma farmacêutica + marca.",
            "warfarin sodium Oral Tablet [Coumadin]"),
    SBDG("Grupo de marca", "Semantic Branded Drug Group",
            "Marca + grupo de forma farmacêutica.",
            "Prozac Pill"),
    SBD("Medicamento de marca", "Semantic Branded Drug",
            "Princípio ativo + concentração + forma farmacêutica + marca.",
            "Fluoxetine 4 MG/ML Oral Solution [Prozac]"),
    BPCK("Embalagem de marca", "Brand Name Pack",
            "Kit de marca com mais de um medicamento, cada um com quantidade, concentração e forma.",
            "{12 (…) / 9 (…) / 7 (…)} Pack [Leena 28 Day]"),
    DF("Forma farmacêutica", "Dose Form",
            "Forma de apresentação do medicamento.",
            "Oral Solution"),
    DFG("Grupo de forma farmacêutica", "Dose Form Group",
            "Conjunto de formas farmacêuticas parecidas.",
            "Oral Liquid"),
    PSN("Nome para prescrição", "Prescribable Name",
            "Sinônimo usado para exibição em sistemas de prescrição eletrônica; no máximo um por conceito.",
            "Leena 28 Day Pack"),
    SY("Sinônimo", "Synonym",
            "Outro nome para o mesmo conceito, dado para clareza.",
            "Prozac 4 MG/ML Oral Solution"),
    TMSY("Sinônimo Tall Man", "Tall Man Lettering Synonym",
            "Destaca trechos em maiúsculas para diferenciar medicamentos de nomes parecidos e evitar trocas.",
            "FLUoxetine 10 MG Oral Capsule [PROzac]"),
    ET("Termo de entrada", "Entry Term",
            "Nome alternativo de forma farmacêutica, ainda sem relação formal com ela.",
            "Nasal Drops");

    private final String label;
    private final String officialName;
    private final String description;
    private final String example;

    RxNormTermType(String label, String officialName, String description, String example) {
        this.label = label;
        this.officialName = officialName;
        this.description = description;
        this.example = example;
    }

    /** Tipo correspondente ao código devolvido pelo RxNav; vazio para código desconhecido. */
    public static Optional<RxNormTermType> fromCode(String code) {
        return Arrays.stream(values())
                .filter(type -> type.name().equals(code))
                .findFirst();
    }

    public String label() {
        return label;
    }

    public String officialName() {
        return officialName;
    }

    public String description() {
        return description;
    }

    public String example() {
        return example;
    }
}
